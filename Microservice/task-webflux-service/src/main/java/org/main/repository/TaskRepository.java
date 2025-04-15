package org.main.repository;

//import main.org.main.model.Task;
//import org.springframework.data.jpa.org.main.repository.JpaRepository;
//import org.springframework.data.jpa.org.main.repository.Query;
import org.main.model.Task;
import org.springframework.data.r2dbc.repository.Query;//switch to RDBC
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.ZonedDateTime;
import java.util.List;

public interface TaskRepository extends ReactiveCrudRepository<Task, Long> {
    Flux<Task> findByUserId(Long userId);

//    @Query("SELECT t FROM Task t WHERE t.expiresAt < :now AND t.isCompleted = false")
//    Flux<Task> findOverdueTasksAndCompletedFalse(@Param("now") ZonedDateTime now);

}
