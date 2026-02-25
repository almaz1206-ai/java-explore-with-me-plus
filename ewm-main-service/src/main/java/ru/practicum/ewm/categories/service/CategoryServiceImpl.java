package ru.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.Category;
import ru.practicum.ewm.categories.CategoryMapper;
import ru.practicum.ewm.categories.CategoryRepository;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.NewCategoryDto;
import ru.practicum.ewm.common.OffsetPageRequest;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.error.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.info("Создаем категорию: name={}", newCategoryDto.getName());
        checkNameUniqueness(newCategoryDto.getName());

        Category saved = categoryRepository.save(CategoryMapper.toCategory(newCategoryDto));
        return CategoryMapper.toCategoryDto(saved);
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        log.info("Обновляем категорию: name={}", categoryDto.getName());
        checkNameUniqueness(categoryDto.getName());

        Category category = getCategory(id);
        category.setName(categoryDto.getName());

        Category updatedCategory = categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
    public List<CategoryDto> getAll(Integer from, Integer size) {
        log.info("Получаем категории: from={}, size={}", from, size);
        Pageable pageable = new OffsetPageRequest(from, size);
        return categoryRepository.findAll(pageable)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCategoryById(Long id) {
        log.info("Удаляем категорию с id={}", id);
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException(String.format("Категория с id: %s не найдена", id));
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDto getById(Long id) {
        log.info("Получаем категорию с id={}", id);
        Category findCategory = getCategory(id);
        return CategoryMapper.toCategoryDto(findCategory);
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id: %s не найдена", id)));
    }

    private void checkNameUniqueness(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new ConflictException(
                    String.format("Категория с наименованием: \"%s\" уже существует", name));
        }
    }
}
