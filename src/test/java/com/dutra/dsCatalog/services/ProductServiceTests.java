package com.dutra.dsCatalog.services;

import com.dutra.dsCatalog.dtos.ProductDto;
import com.dutra.dsCatalog.entities.Category;
import com.dutra.dsCatalog.entities.Product;
import com.dutra.dsCatalog.utils.Factory;
import com.dutra.dsCatalog.repositories.CategoryRepository;
import com.dutra.dsCatalog.repositories.ProductRepository;
import com.dutra.dsCatalog.services.exceptions.DataBaseException;
import com.dutra.dsCatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl page;
    private Product product;
    private ProductDto productDto;
    private Category category;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1;
        nonExistingId = 2L;
        dependentId = 3L;

        productDto = new ProductDto(Factory.createProduct());
        product = Factory.createProduct();
        category = Factory.createCategory();

        page = new PageImpl<>(List.of(product));


        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);


        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());


        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(ResourceNotFoundException.class).when(repository).deleteById(nonExistingId);
        Mockito.doThrow(DataBaseException.class).when(repository).deleteById(dependentId);

        Mockito.doThrow(ResourceNotFoundException.class).when(repository).getReferenceById(nonExistingId);
        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);

        Mockito.doThrow(ResourceNotFoundException.class).when(categoryRepository).getReferenceById(nonExistingId);
        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);

    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

        Mockito.verify(repository, Mockito.times(0)).deleteById(nonExistingId);
    }

    @Test
    public void deleteShouldThrowDataBaseExceptionDependentId() {
        Assertions.assertThrows(DataBaseException.class, () -> {
            service.delete(dependentId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
    }

    @Test
    public void findAllPageShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductDto> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void findByIdShouldReturnProductDtoWhenIdExists() {
        ProductDto result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowsExceptionWhenIdDoNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });

        Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
    }

    @Test
    public void updateShouldReturnProductDtoWhenIdExists() {
        ProductDto result = service.updateProduct(existingId, productDto);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).getReferenceById(existingId);
    }

    @Test
    public void updateShouldThrowsExceptionWhenIdDoNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.updateProduct(nonExistingId, productDto);
        });

        Mockito.verify(repository, Mockito.times(1)).getReferenceById(nonExistingId);
    }

}
