package main.service;

import main.model.Notification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {
    List<Notification> getAllNotifications();

    List<Notification> getNotificationsByUserId(long userId);

    List<Notification> getNotificationsByTaskId(long userId);

}
