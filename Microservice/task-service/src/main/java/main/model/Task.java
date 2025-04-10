package main.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.io.Serializable;
import java.time.ZonedDateTime;


@Getter
@Entity
@NoArgsConstructor //mandatory for SpringJPA
@Table(name = "tasks")
public class Task implements Serializable  { //serializable simple but not the best solution?

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    long taskId;
    @Positive @NotNull //can't use foreign keys tables are in different databases
    long userId;
    @NotEmpty @Column(length = 100)
    String taskTitle;
    @NotEmpty @Column(length = 255)
    String taskDescription;
    @Column
    final ZonedDateTime createdAt = ZonedDateTime.now(); // do I want to remove default now? since postgres can autogen it
    @NotNull @Column
    ZonedDateTime expiresAt;
    @Column
    boolean isCompleted = false; //need to implement update request to change that. Or some other logic

    @AssertTrue(message = "expiresAt must be after createdAt") //No message is shown, but the check works
    private boolean isExpiresAtValid() {
        return expiresAt == null || expiresAt.isAfter(createdAt);
    }

    public Task(long id, long userId, String taskTitle, String taskDescription, ZonedDateTime expiresAt) {
        this.taskId = id;
        this.userId = userId;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.expiresAt = expiresAt;
        // createdAt = .now default
        // isCompleted = false default
    }
}
