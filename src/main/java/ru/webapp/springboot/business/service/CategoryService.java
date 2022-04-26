package ru.webapp.springboot.business.service;

import org.springframework.stereotype.Service;
import ru.webapp.springboot.business.entity.Category;
import ru.webapp.springboot.business.repository.CategoryRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category add(Category category) {
        return categoryRepository.save(category);
    }

    public void update(Category category) {
        categoryRepository.save(category);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    public List<Category> findAll(String email) {
        return categoryRepository.findByUserEmailOrderByTitleAsc(email);
    }

    public List<Category> find(String title, String email) {
        return categoryRepository.find(title, email);
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id).get();
    }

}
