package com.alexhiz.hexagonal.helpdesk.category.application.service;

import com.alexhiz.hexagonal.helpdesk.category.application.port.in.*;
import com.alexhiz.hexagonal.helpdesk.category.application.port.out.CategoryRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.category.domain.exception.CategoryAlreadyExistsException;
import com.alexhiz.hexagonal.helpdesk.category.domain.exception.CategoryNotFoundException;
import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import com.alexhiz.hexagonal.helpdesk.department.application.port.out.DepartmentRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.department.domain.exception.DepartmentNotFoundException;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService implements CreateCategoryUseCase, UpdateCategoryUseCase, GetCategoryByIdUseCase, ListCategoriesUseCase, DeleteCategoryUseCase, GetCategoriesByDepartmentUseCase, PageCategoriesUseCase  {
    private static final String CATEGORIES_CACHE_KEY = "categories";
    private static final Duration  CACHE_TTL = Duration.ofMinutes(10);
    private final RedisTemplate<String, Object> redisTemplate;

    private final CategoryRepositoryPort categoryRepositoryPort;
    private final DepartmentRepositoryPort departmentRepositoryPort;

    @Override
    @Transactional
    public Category create(Category category) {
        var name = category.getName().trim();
        var deparmentId = category.getDepartmentId();
        if(categoryRepositoryPort.existsByName(name)){
            throw new  CategoryAlreadyExistsException(name);
        }
        if(!departmentRepositoryPort.existsById(deparmentId)){
            throw new DepartmentNotFoundException(deparmentId);
        }
        category.setName(name);
        Category saved = categoryRepositoryPort.save(category);
        evictCategoriesCache();
        return saved;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if(!categoryRepositoryPort.existsById(id)){
            throw  new CategoryNotFoundException(id);
        }
        evictCategoriesCache();
        categoryRepositoryPort.delete(id);
    }

    @Override
    public Category getCategoryById(UUID id) {
        return categoryRepositoryPort.findById(id).orElseThrow(()-> new CategoryNotFoundException(id));
    }

    @Override
    public List<Category> getAllCategories() {
        return getAllCategoriesFromCache();
    }

    @SuppressWarnings("unchecked")
    public List<Category> getAllCategoriesFromCache(){
        //cuando es completo
        try {
            Object cachedData = redisTemplate.opsForValue().get(CATEGORIES_CACHE_KEY);
            if(cachedData instanceof List<?> list && !list.isEmpty()){
                log.info("Redis Cache HIT: Categories recuperados desde la caché");
                return (List<Category>) cachedData;
            }
        } catch (Exception e) {
            log.error("Error al consultar la caché de Redis, fallback a base de datos: {}", e.getMessage());
        }

        log.info("Redis Cache MISS: Consultando base de datos para categories");
        List<Category> categories = categoryRepositoryPort.findAll();
        try {
            if(categories != null && !categories.isEmpty()){
                redisTemplate.opsForValue().set(CATEGORIES_CACHE_KEY, categories, CACHE_TTL);
                log.info("Redis Cache UPDATED: categories almacenados en caché con TTL de {}", CACHE_TTL);
            }
        } catch (Exception e) {
            log.error("Error al actualizar la caché de Redis: {}", e.getMessage());
        }
        return categories;
    }



    @Override
    @Transactional
    public Category update(UUID id, Category category) {
        Category cat = categoryRepositoryPort.findById(id).orElseThrow(()-> new CategoryNotFoundException(id));
        var name = category.getName().trim();
        var deparmentId = category.getDepartmentId();
        if(categoryRepositoryPort.existsByNameAndIdNot(name, id)){
            throw new CategoryAlreadyExistsException(name);
        }
        if(!departmentRepositoryPort.existsById(deparmentId)){
            throw new DepartmentNotFoundException(deparmentId);
        }
        cat.setName(name);
        cat.setDepartmentId(deparmentId);
        cat.setActive(category.getActive());
        Category saved = categoryRepositoryPort.save(cat);
        evictCategoriesCache();
        return saved;
    }

    private void evictCategoriesCache(){
        try {
            Boolean deleted = redisTemplate.delete(CATEGORIES_CACHE_KEY);
            log.info("Redis cache EVICTED: clave '{}' eliminada : {}", CATEGORIES_CACHE_KEY, deleted);
        }catch (Exception e){
            log.error("Error al invalidar la caché de Redis: {}", e.getMessage());
        }
    }

    @Override
    public List<Category> getAllCategoriesByDepartment(UUID departmentId) {
        if (!departmentRepositoryPort.existsById(departmentId)) {
            throw new DepartmentNotFoundException(departmentId);
        }
        return  categoryRepositoryPort.findByDepartmentIdAndActiveTrue(departmentId);
    }

    @Override
    public PageResult<Category> execute(PageQuery pageQuery) {
        return getCategoriesPageFromCache(pageQuery);
    }

    @SuppressWarnings("unchecked")
    public PageResult<Category> getCategoriesPageFromCache(PageQuery pageQuery) {
        // 1. Crear una clave dinámica según la página y tamaño
        String cacheKey = CATEGORIES_CACHE_KEY + ":page:" + pageQuery.page() + ":size:" + pageQuery.size();

        try {
            Object cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData instanceof PageResult<?> cachedPage && !cachedPage.content().isEmpty()) {
                log.info("Redis Cache HIT: Categories recuperados desde la caché para key: {}", cacheKey);
                return (PageResult<Category>) cachedPage;
            }
        } catch (Exception e) {
            log.error("Error al consultar la caché de Redis, fallback a base de datos: {}", e.getMessage());
        }

        log.info("Redis Cache MISS: Consultando base de datos para categories paginados");
        PageResult<Category> pageResult = categoryRepositoryPort.findAllPages(pageQuery);

        try {
            if (pageResult != null && !pageResult.content().isEmpty()) {
                redisTemplate.opsForValue().set(cacheKey, pageResult, CACHE_TTL);
                log.info("Redis Cache UPDATED: Key {} almacenada en caché con TTL de {}", cacheKey, CACHE_TTL);
            }
        } catch (Exception e) {
            log.error("Error al actualizar la caché de Redis: {}", e.getMessage());
        }

        return pageResult;
    }
}
