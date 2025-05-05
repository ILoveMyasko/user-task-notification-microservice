package main.service;
import com.fasterxml.jackson.databind.ObjectMapper;

import main.exceptions.*;
import main.model.Task;
import main.repository.TaskRepository;
import org.main.CommonEvents.TaskEvent; //from common lib
import org.main.CommonEvents.TaskEventTypeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.List;


@Service
public class TaskServiceImpl implements TaskService {

    final private TaskRepository taskRepository;
    final RestTemplate restTemplate;
    final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${user-service.base-url}") // @Value not from Lombok but Spring
    private String userServiceBaseUrl; //kinda hardcoded the URL

    public TaskServiceImpl(TaskRepository taskRepository, KafkaTemplate<String, Object> kafkaTemplate, RestTemplate restTemplate) {
        this.taskRepository = taskRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
    }

    //so many org.main.exceptions
    @Caching(
            put = @CachePut(value = "tasks", key = "#result.taskId"), //working with database generation
            evict = @CacheEvict(value = "userTasks", key = "#result.userId") // or task?
    )
    public Task createTask(Task task) {
        try {
            restTemplate.getForEntity(userServiceBaseUrl + "/users/" + task.getUserId(), String.class); //hardcoded and looks unsafe
        } catch (RuntimeException ex) {
            if (ex.getClass() == HttpClientErrorException.NotFound.class) {
                throw new TaskCreationUserNotExistsException(ex.getMessage());
            } else {
                throw new TaskCreationUserServiceUnavailableException("Error communicating with User service: " + ex.getMessage());
            }
        }
        if (taskRepository.existsById(task.getTaskId())) { //TODO deal with caching
            throw new TaskAlreadyExistsException(task.getTaskId());
        }
        // do we save or make kafka do it?
        Task savedTask = taskRepository.save(task); // TODO service can be unavailable
        TaskEvent taskEventCreated = new TaskEvent(TaskEventTypeEnum.CREATE, savedTask.getTaskId(), savedTask.getUserId());
        //actually won't allow me to post anything if kafka is down
        kafkaTemplate.send("task-events", taskEventCreated); // if kafka is not up we do what? make it transactional?
        return savedTask;
    }

    @Cacheable(value = "tasks", key = "#id")
    public Task getTaskById(long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Cacheable(value = "userTasks", key = "#id")
    public List<Task> getTasksByUserId(long id) {
        return taskRepository.findByUserId(id);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "tasks", key = "#id"),
                    @CacheEvict(value = "userTasks", key = "#result.userId")
            })
    public Task deleteTaskById(long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.deleteById(id);
        TaskEvent taskEventDeleted = new TaskEvent(TaskEventTypeEnum.DELETE, task.getTaskId(), task.getUserId());
        kafkaTemplate.send("task-events", taskEventDeleted);//.get or we might get in trouble. .whenComplete?
        return task;
    }

    @Async
    @Scheduled(initialDelay = 60000, fixedDelay = 60000)
    void deleteOverdueTasks() {
        System.out.println("Deleting overdue tasks...");
        List<Task> overdueTasks = taskRepository.findOverdueTasksAndCompletedFalse(ZonedDateTime.now());
        if (!overdueTasks.isEmpty()) {
            taskRepository.deleteAll(overdueTasks);

            overdueTasks.forEach(overdueTask -> kafkaTemplate
                    .send("task-events", new TaskEvent(TaskEventTypeEnum.DELETE, overdueTask.getTaskId(), overdueTask.getUserId())));

        }
    }
}
