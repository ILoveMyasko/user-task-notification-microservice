package main.repository;

import main.model.Task;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);

    @Query("SELECT t FROM Task t WHERE t.expiresAt < :now AND t.isCompleted = false")
    List<Task> findOverdueTasksAndCompletedFalse(@Param("now") ZonedDateTime now);

//    @Cacheable(value = "taskExistence", key = "#taskId") //why key is always 0
//    boolean existsByTaskId(Long taskId);
}
