package com.dutra.dsCatalog.services;

import com.dutra.dsCatalog.dtos.CategoryDto;
import com.dutra.dsCatalog.dtos.ProductDto;
import com.dutra.dsCatalog.entities.Category;
import com.dutra.dsCatalog.entities.Product;
import com.dutra.dsCatalog.repositories.CategoryRepository;
import com.dutra.dsCatalog.repositories.ProductRepository;
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

@Service
public class ProductService {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> findAllPaged(Pageable pageable) {
        return repository.findAll(pageable).map(product -> new ProductDto(product));
    }

    @Transactional(readOnly = true)
    public ProductDto findById(Long id) {
        Product product = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found!")
        );
        return new ProductDto(product, product.getCategories());
    }

    @Transactional
    public ProductDto save(ProductDto newProduct) {
        Product product = new Product();
        return new ProductDto(repository.save(builderProduct(product, newProduct)));
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productIn) {
        try {
            Product product = repository.getReferenceById(id);

            return new ProductDto(repository.save(builderProduct(product, productIn)));
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

    private Product builderProduct(Product product, ProductDto productDto) {
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImgUrl(productDto.getImgUrl());

        product.getCategories().clear();
        for (CategoryDto categoryDto : productDto.getCategories()) {
            Category category = categoryRepository.getReferenceById(categoryDto.getId());
            product.getCategories().add(category);
        }

        return product;
    }
}
