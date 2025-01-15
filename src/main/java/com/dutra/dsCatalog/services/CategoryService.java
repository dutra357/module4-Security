package com.dutra.dsCatalog.services;

import com.dutra.dsCatalog.dtos.CategoryDto;
import com.dutra.dsCatalog.entities.Category;
import com.dutra.dsCatalog.repositories.CategoryRepository;
import com.dutra.dsCatalog.services.exceptions.DataBaseException;
import com.dutra.dsCatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<CategoryDto> findAllPaged(Pageable pageable) {
        return repository.findAll(pageable).map(category -> new CategoryDto(category));
    }

    @Transactional(readOnly = true)
    public CategoryDto findById(Long id) {
        return new CategoryDto(repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category not found!")
        ));
    }

    @Transactional
    public CategoryDto save(CategoryDto newCategory) {
        Category category = new Category();
        category.setName(newCategory.getName());

        return new CategoryDto(repository.save(category));
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryIn) {
        try {
            Category category = repository.getReferenceById(id);

            category.setName(categoryIn.getName());

            return new CategoryDto(repository.save(category));
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("ID not found!");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found!");
        }

        try {
            repository.deleteById(id);

        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Referential integrity violation.");
        }
    }
}
