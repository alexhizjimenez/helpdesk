package com.alexhiz.hexagonal.helpdesk.category.application.service;

import com.alexhiz.hexagonal.helpdesk.category.application.port.out.CategoryRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.category.domain.exception.CategoryAlreadyExistsException;
import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import com.alexhiz.hexagonal.helpdesk.department.application.port.out.DepartmentRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.department.domain.exception.DepartmentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryServiceTest {
    private static final String CATEGORIES_CACHE_KEY = "categories";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private CategoryRepositoryPort mockCategoryRepo;
    private DepartmentRepositoryPort mockDepartmentRepo;
    private CategoryService categoryService;
    private RedisTemplate<String, Object> redisTemplate;


    @BeforeEach
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        mockCategoryRepo = mock(CategoryRepositoryPort.class);
        mockDepartmentRepo = mock(DepartmentRepositoryPort.class);
        categoryService = new CategoryService(redisTemplate, mockCategoryRepo, mockDepartmentRepo);
    }

    @Test
    void shouldCreateCategory() {
        Category category = Category.builder().name("Test")
                .publicId(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                .departmentId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .active(true).build();
        Category expected = Category.builder()
                .id(1L)
                .publicId(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                .name("Test")
                .departmentId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();

        when(mockCategoryRepo.existsByName("Test")).thenReturn(false);
        when(mockDepartmentRepo.existsById(UUID.fromString("11111111-1111-1111-1111-111111111111"))).thenReturn(true);
        when(mockCategoryRepo.save(any(Category.class))).thenReturn(expected);



        Category result = categoryService.create(category);
        assertNotNull(result);
        assertEquals(expected.getId(), result.getId());
        assertEquals("Test", result.getName());
        assertEquals(expected.getDepartmentId(), result.getDepartmentId());

        verify(mockCategoryRepo).existsByName("Test");
        verify(mockDepartmentRepo).existsById(expected.getDepartmentId());
        verify(mockCategoryRepo).save(any(Category.class));
    }

    @Test
    void shouldThrowExceptionCategoryAlreadyExists() {
        Category category = Category.builder().name("Test")
                .publicId(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                .departmentId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .active(true).build();

        when(mockCategoryRepo.existsByName("Test")).thenReturn(true);

        CategoryAlreadyExistsException categoryAlreadyExistsException = assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.create(category));

        assertEquals("Category already exists with name: "+ "Test", categoryAlreadyExistsException.getMessage());
        verify(mockCategoryRepo).existsByName("Test");
        verifyNoMoreInteractions(mockCategoryRepo);

    }

    @Test
    void shouldThrowExceptionDepartmentNotFound() {
        UUID departmentId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Category category = Category.builder().name("Test")
                .publicId(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                .departmentId(departmentId)
                .active(true).build();

        when(mockCategoryRepo.existsByName("Test")).thenReturn(false);
        when(mockDepartmentRepo.existsById(departmentId)).thenReturn(false);

        DepartmentNotFoundException exception = assertThrows(DepartmentNotFoundException.class, () -> categoryService.create(category));

        assertNotNull(exception);
        assertEquals("Department not found with id: " + departmentId, exception.getMessage());
        verify(mockCategoryRepo).existsByName("Test");
        verify(mockDepartmentRepo).existsById(departmentId);
        verify(mockCategoryRepo, never()).save(any());
    }

    @Test
    void shouldDeleteCategory() {
        UUID categoryId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        when(mockCategoryRepo.existsById(categoryId)).thenReturn(true);
        doNothing().when(mockCategoryRepo).delete(categoryId);

        categoryService.delete(categoryId);
        verify(mockCategoryRepo).existsById(categoryId);
        verify(mockCategoryRepo).delete(categoryId);
    }

}