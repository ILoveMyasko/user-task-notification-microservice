package org.main.service;



import org.main.exceptions.TaskAlreadyExistsException;
import org.main.exceptions.TaskCreationUserNotExistsException;
import org.main.exceptions.TaskCreationUserServiceUnavailableException;
import org.main.model.Task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.main.repository.TaskRepository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import org.main.CommonEvents.TaskEvent; //from common lib
import org.main.CommonEvents.TaskEventTypeEnum;
import java.time.ZonedDateTime;
import java.util.List;


@Service
public class TaskServiceImpl implements TaskService {

    final private TaskRepository taskRepository;
    //final RestTemplate restTemplate;
    private final WebClient webClient; // non-blocking for webflux
    final KafkaTemplate<String, Object> kafkaTemplate;
    public TaskServiceImpl(TaskRepository taskRepository, KafkaTemplate<String, Object> kafkaTemplate, WebClient userServiceWebClient) {
        this.taskRepository = taskRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.webClient = userServiceWebClient;
    }

    //so many org.main.exceptions
    @Caching(
            put = @CachePut(value = "tasks", key = "#result.taskId"), //working with database generation
            evict = @CacheEvict(value = "userTasks", key = "#result.userId") // or task?
    )
    public Mono<Task> createTask(Task task) {
        // 1. Check if user exists via WebClient
        Mono<Void> userCheck = webClient.get()
                .uri("/users/{userId}", task.getUserId()) // base path in config
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, // Predicate for 404
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("[User Service Body Empty on 404]") //TODO check and remove
                                .flatMap(body -> Mono.error(
                                        new TaskCreationUserNotExistsException("User service returned 404. Body: " + body))))
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("[User Service Body Empty on Error]")
                                .flatMap(body -> Mono.error(
                                        new TaskCreationUserServiceUnavailableException(
                                                "User service error: Status " + clientResponse.statusCode() + ", Body: " + body))))
                .bodyToMono(Void.class) // kinda like else here, emits onComplete. necessary to release the connection
                .onErrorResume(WebClientRequestException.class,
                        ex -> Mono.error(new TaskCreationUserServiceUnavailableException("Cannot connect to User service: " + ex.getMessage()))
                ); //TODO check how it works
        return userCheck
                .then(taskRepository.existsById(task.getTaskId())) //return Mono<Boolean>
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new TaskAlreadyExistsException(task.getTaskId()));
                    } else {
                        return taskRepository.save(task); // return for flow not whole function
                    }
                })
               // if returned saved task
                .flatMap(savedTask -> { // Use flatMap if PUT is reactive, otherwise doOnSuccess
                    // TODO: Reactive Cache PUT logic replaces @CachePut
                    // Example Placeholder: reactiveCacheManager.getCache("tasks").put(savedTask.getTaskId(), savedTask);
                    // This placeholder assumes cache put returns Mono<Void> or similar.
                    // For simplicity now, just pass the task through.
                    return Mono.just(savedTask);
                })
                .doOnSuccess(savedTask -> { //producing side effects
                    // a) TODO: Reactive Cache EVICT logic replaces @CacheEvict
                    // Example Placeholder: reactiveCacheManager.getCache("userTasks").evict(savedTask.getUserId()).subscribe();
                    // Eviction is often fire-and-forget, triggered via subscribe().
                    TaskEvent taskEventCreated = new TaskEvent(TaskEventTypeEnum.CREATE, savedTask.getTaskId(), savedTask.getUserId());
                    kafkaTemplate.send("task-events", taskEventCreated);
                    // Standard kafkaTemplate.send is non-blocking enough for many cases here.
                    // For fully reactive Kafka: use ReactiveKafkaProducerTemplate.
                });
    }

//    @Cacheable(value = "tasks", key = "#id")
//    public Task getTaskById(long id) {
//        return taskRepository.findById(id)
//                .orElseThrow(() -> new TaskNotFoundException(id));
//    }
//
//    @Cacheable(value = "userTasks", key = "#id")
//    public List<Task> getTasksByUserId(long id) {
//        return taskRepository.findByUserId(id);
//    }
//
//    public List<Task> getAllTasks() {
//        return taskRepository.findAll();
//    }
//
//    @Caching(
//            evict = {
//                    @CacheEvict(value = "tasks", key = "#id"),
//                    @CacheEvict(value = "userTasks", key = "#result.userId")
//            })
//    public Task deleteTaskById(long id) {
//        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
//        taskRepository.deleteById(id);
//        TaskEvent taskEventDeleted = new TaskEvent(TaskEventTypeEnum.DELETE, task.getTaskId(), task.getUserId());
//        kafkaTemplate.send("task-events", taskEventDeleted);
//        return task;
//    }
//
//    @Async
//    @Scheduled(initialDelay = 30000, fixedDelay = 60000)
//    void deleteOverdueTasks() {
//        System.out.println("Deleting overdue tasks...");
//        List<Task> overdueTasks = taskRepository.findOverdueTasksAndCompletedFalse(ZonedDateTime.now());
//        if (!overdueTasks.isEmpty()) {
//            taskRepository.deleteAll(overdueTasks);
//
//            overdueTasks.forEach(overdueTask -> kafkaTemplate
//                    .send("task-events", new TaskEvent(TaskEventTypeEnum.DELETE, overdueTask.getTaskId(), overdueTask.getUserId())));
//
//        }
//    }
}
