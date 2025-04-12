package main.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long notificationId;
    private @Positive @Column
    long userId;
    private @Positive  @Column
    long taskId;
    @NotEmpty @Column(length = 255)
    private String text;
    @Column
    final private LocalDateTime createdAt = LocalDateTime.now();

    public Notification(long notificationId, long userId, long taskId, String text) {
        this.notificationId =notificationId;
        this.userId = userId;
        this.taskId = taskId;
        this.text = text;
        // createdAt = .now default
    }
}
