package main.repository;

import main.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByTaskId(Long taskId);

   // @Transactional
    void deleteByTaskId(Long taskId);
}
