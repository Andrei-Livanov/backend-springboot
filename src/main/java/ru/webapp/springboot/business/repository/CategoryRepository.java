package ru.webapp.springboot.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.webapp.springboot.business.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserEmailOrderByTitleAsc(String email);

    @Query("SELECT c FROM Category c WHERE " +
            "(:title IS NULL OR :title='' " +
            "OR LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND c.user.email=:email " +
            "ORDER BY c.title ASC")
    List<Category> find(@Param("title") String title, @Param("email") String email);

}
