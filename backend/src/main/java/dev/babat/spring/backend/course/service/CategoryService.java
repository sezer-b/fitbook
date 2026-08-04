package dev.babat.spring.backend.course.service;

import dev.babat.spring.backend.course.dto.CategoryDTO;
import dev.babat.spring.backend.course.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAll() {
        return categoryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(c -> new CategoryDTO(c.getId(), c.getName()))
                .toList();
    }
}
