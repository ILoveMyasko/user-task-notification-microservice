package main.service;

import jakarta.transaction.Transactional;
import main.exceptions.RequestedEntityNotFoundException;
import main.exceptions.ServiceRequestFailedException;
import main.model.Notification;
import main.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.main.CommonEvents.TaskEvent; //from common lib
import org.main.CommonEvents.TaskEventTypeEnum;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    final private NotificationRepository notificationRepository;

    final RestTemplate restTemplate;

    @Value("${user-service.base-url}")
    private String userServiceBaseUrl;

    @Value("${task-service.base-url}")
    private String taskServiceBaseUrl;

    public NotificationServiceImpl(NotificationRepository notificationRepository, RestTemplate restTemplate) {
        this.notificationRepository = notificationRepository;
        this.restTemplate = restTemplate;
    }


    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }


    public List<Notification> getNotificationsByUserId(long userId) {
        checkUserEntityExistence(userId);
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> getNotificationsByTaskId(long taskId) {
        checkTaskEntityExistence(taskId);
        return notificationRepository.findByTaskId(taskId);
    }


//    public Notification createNotification(Notification notification) { //remove after adding kafka?
//        checkUserEntityExistence(notification.getUserId());
//        checkTaskEntityExistence(notification.getTaskId());
//        return notificationRepository.save(notification);
//    }

    public void checkTaskEntityExistence(long taskId) {
        try {
            restTemplate.getForEntity(taskServiceBaseUrl + "/tasks/" + taskId, String.class);
        } catch (RuntimeException ex) {
            if (ex.getClass() == HttpClientErrorException.NotFound.class) {
                throw new RequestedEntityNotFoundException(ex.getMessage());
            } else {
                throw new ServiceRequestFailedException("Error communicating with Task service: " + ex.getMessage());
            }
        }
    }

    public void checkUserEntityExistence(long userId) {
        try {
            restTemplate.getForEntity(userServiceBaseUrl + "/users/" + userId, String.class);
        } catch (RuntimeException ex) {
            if (ex.getClass() == HttpClientErrorException.NotFound.class) {
                throw new RequestedEntityNotFoundException(ex.getMessage());
            } else {
                throw new ServiceRequestFailedException("Error communicating with User service: " + ex.getMessage());
            }
        }
    }


    @KafkaListener(topics = "task-events")
    @Transactional //get errors without it
    public void handleTaskEvent(TaskEvent taskEvent) {
        switch (taskEvent.eventType()) {
            case CREATE: {
                Notification notification = new Notification(); // notificationId will be fine
                notification.setTaskId(taskEvent.taskId());
                notification.setUserId(taskEvent.userId());
                notification.setText("Task " + taskEvent.taskId() + " created");
                notificationRepository.save(notification);
                System.out.println("saved notification:" + notification);
                break;
            }
            case DELETE: {
                notificationRepository.deleteByTaskId(taskEvent.taskId());
                break;
            }
            default: {
                System.out.println("No actions implemented for event type: " + taskEvent.eventType());
            }
        }


    }

}
