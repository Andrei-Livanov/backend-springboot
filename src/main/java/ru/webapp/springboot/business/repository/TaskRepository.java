package ru.webapp.springboot.business.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.webapp.springboot.business.entity.Task;

import java.util.Date;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserEmailOrderByTitleAsc(String email);

    @Query("SELECT t FROM Task t WHERE " +
            "(:title iS NULL OR :title='' OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title,'%'))) AND " +
            "(:completed iS NULL OR t.completed=:completed) AND " +
            "(:priorityId iS NULL OR t.priority.id=:priorityId) AND " +
            "(:categoryId iS NULL OR t.category.id=:categoryId) AND " +
            "(" +
            "(cast(:dateFrom AS timestamp) iS NULL OR t.taskDate>=:dateFrom) AND " +
            "(cast(:dateTo AS timestamp) iS NULL OR t.taskDate<=:dateTo)" +
            ") AND " +
            "(t.user.email=:email)"
    )
    Page<Task> find(@Param("title") String title,
                    @Param("completed") Integer completed,
                    @Param("priorityId") Long priorityId,
                    @Param("categoryId") Long categoryId,
                    @Param("email") String email,
                    @Param("dateFrom") Date dateFrom,
                    @Param("dateTo") Date dateTo,
                    Pageable pageable
    );

}
