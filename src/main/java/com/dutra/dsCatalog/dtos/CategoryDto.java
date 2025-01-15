package com.dutra.dsCatalog.dtos;

import com.dutra.dsCatalog.entities.Category;

import java.io.Serializable;

public class CategoryDto implements Serializable {

    private Long id;
    private String name;

    public CategoryDto() {}
    public CategoryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
