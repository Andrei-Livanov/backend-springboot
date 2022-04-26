package ru.webapp.springboot.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.webapp.springboot.business.entity.Priority;

import java.util.List;

@Repository
public interface PriorityRepository extends JpaRepository<Priority, Long> {

    List<Priority> findByUserEmailOrderByIdAsc(String email);

    @Query("SELECT p FROM Priority p WHERE " +
            "(:title IS NULL OR :title='' " +
            "OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND p.user.email=:email " +
            "ORDER BY p.title ASC")
    List<Priority> find(@Param("title") String title, @Param("email") String email);

}
