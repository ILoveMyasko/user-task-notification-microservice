package main.service;
import main.exceptions.TaskCreationErrorException;
import main.kafkaData.TaskEvent;
import main.kafkaData.TaskEventTypeEnum;
import main.model.Task;
import main.exceptions.TaskAlreadyExistsException;
import main.exceptions.TaskNotFoundException;
import main.repository.TaskRepository;
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


    @Caching(
            put = @CachePut(value = "tasks", key = "#result.taskId"),
            evict = @CacheEvict(value = "userTasks", key = "#result.userId")//TODO: check later
    )
    public Task createTask(Task task) {
        try {
            restTemplate.getForEntity(userServiceBaseUrl + "/users/" + task.getUserId(), String.class);
        } catch (RuntimeException ex) {
            if (ex.getClass() == HttpClientErrorException.NotFound.class) {
                throw new TaskCreationErrorException("User with ID " + task.getUserId() + " does not exist."); //TODO need more than one exception anyway
            } else {
                throw new TaskCreationErrorException("Error communicating with UserService: " + ex.getMessage());
            }
        }
        if (taskRepository.existsById(task.getTaskId())) { // TODO: use cache?
            throw new TaskAlreadyExistsException(task.getTaskId());
        }
        Task savedTask = taskRepository.save(task);
        kafkaTemplate.send("task-events", new TaskEvent(TaskEventTypeEnum.CREATE, savedTask.getTaskId(), savedTask.getUserId()));
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

    //no caching
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "tasks", key = "#result.taskId"), //TODO check keys
                    @CacheEvict(value = "userTasks", key = "#result.userId")
            })
    public Task deleteTaskById(long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.deleteById(id);
        return task;
    }

    @Async
    @Scheduled(initialDelay = 30000, fixedDelay = 60000)
    void deleteOverdueTasks() {
        List<Task> overdueTasks = taskRepository.findByDueDateBeforeAndCompletedFalse(ZonedDateTime.now());
        if (!overdueTasks.isEmpty()) {
            taskRepository.deleteAll(overdueTasks);

            overdueTasks.forEach(overdueTask -> kafkaTemplate
                    .send("task-events", new TaskEvent(TaskEventTypeEnum.DELETE, overdueTask.getTaskId(), overdueTask.getUserId())));

        }
    }
}
