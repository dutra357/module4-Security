package com.dutra.dsCatalog.integration;

import com.dutra.dsCatalog.dtos.ProductDto;
import com.dutra.dsCatalog.utils.Factory;
import com.dutra.dsCatalog.utils.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;

    private String bearerToken;
    private Long existingId;
    private Long notExistingId;
    private Long totalProducts;
    private ProductDto productDto;
    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        notExistingId = 999L;
        totalProducts = 25L;

        productDto = Factory.createProductDto();

        bearerToken = tokenUtil.obtainAccessToken(mockMvc, "maria@gmail.com", "123456");

    }

    @Test
    public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders.get("/products?page=0&size=12&sort=name,asc")
                        .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$..totalElements").value(25));
    }

    @Test
    public void updateShouldReturnProductDtoWhenIdExists() throws Exception {
        String productJson = objectMapper.writeValueAsString(productDto);

        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders.put("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + bearerToken)
                        .content(productJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.name").value(productDto.getName()));
        resultActions.andExpect(jsonPath("$.description").value(productDto.getDescription()));
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() throws Exception {
        String productJson = objectMapper.writeValueAsString(productDto);

        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders.put("/products/{id}", notExistingId)
                        .header("Authorization", "Bearer " + bearerToken)
                        .content(productJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }
}
