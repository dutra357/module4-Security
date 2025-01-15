package com.dutra.dsCatalog.repositories;

import com.dutra.dsCatalog.entities.Product;
import com.dutra.dsCatalog.utils.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long notExistingId;
    private Long totalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        notExistingId = 999L;
        totalProducts = 25L;

    }

    @Test
    public void deleteShouldObjectWhenIdExists() {

        repository.deleteById(existingId);

        Optional<Product> result = repository.findById(existingId);

        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void saveShouldPersistWhithAutoincrementWhenIdIsNull() {
        Product product = Factory.createProduct();

        product.setId(null);

        product = repository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals((totalProducts + 1), product.getId());

    }

    @Test
    public void findByIdShouldReturnNullWhenIdDoNotExists() {
        Optional<Product> product = repository.findById(existingId);
        Assertions.assertNotNull(product.get());
    }

    @Test
    public void findByIdShouldReturnNotNullWhenIdExists() {
        Optional<Product> product = repository.findById(notExistingId);
        Assertions.assertFalse(product.isPresent());
    }
}
