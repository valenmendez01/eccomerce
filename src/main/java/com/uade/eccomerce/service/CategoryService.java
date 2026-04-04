
package com.uade.eccomerce.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.eccomerce.entity.Categoria;

public interface CategoryService {
    public Page<Categoria> getCategories(PageRequest pageRequest);

    public Optional<Categoria> getCategoryById(Long categoryId);
}