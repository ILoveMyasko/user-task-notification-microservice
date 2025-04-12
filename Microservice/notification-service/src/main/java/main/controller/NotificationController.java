package main.controller;


import main.exceptions.RequestedEntityNotFoundException;
import main.exceptions.ServiceRequestFailedException;
import main.model.Notification;
import main.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class

NotificationController {

    private final NotificationService notificationService;

    NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications(){
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable("id") long uId){
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(uId));
    }
    @GetMapping("/tasks/{id}")
    public ResponseEntity<List<Notification>> getNotificationsByTaskId(@PathVariable("id") long uId){
        return ResponseEntity.ok(notificationService.getNotificationsByTaskId(uId));
    }
    @ExceptionHandler(ServiceRequestFailedException.class)
    public ResponseEntity<String> handleServiceRequestFailedException(ServiceRequestFailedException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());// a stub
    }
    @ExceptionHandler(RequestedEntityNotFoundException.class)
    public ResponseEntity<String> handleRequestedEntityNotFoundException(RequestedEntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());// a stub
    }

}
