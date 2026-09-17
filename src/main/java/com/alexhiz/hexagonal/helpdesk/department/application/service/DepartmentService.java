package com.alexhiz.hexagonal.helpdesk.department.application.service;

import com.alexhiz.hexagonal.helpdesk.department.application.port.in.*;
import com.alexhiz.hexagonal.helpdesk.department.application.port.out.DepartmentRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.department.domain.exception.DepartmentAlreadyExistsException;
import com.alexhiz.hexagonal.helpdesk.department.domain.exception.DepartmentNotFoundException;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.BusinessException;
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
public class DepartmentService implements CreateDepartmentUseCase, ListDepartmentsUseCase, GetDepartmentByIdUseCase,
        UpdateDepartmentUseCase, DeleteDepartmentUseCase, PageDepartmentsUseCase {

    private static final String DEPARTMENTS_CACHE_KEY = "departments";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final DepartmentRepositoryPort departmentRepositoryPort;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public Department create(Department department) {
        if (department.getName() == null || department.getName().trim().isEmpty()) {
            throw new BusinessException("Department name cannot be empty");
        }
        var name = department.getName().trim();

        if (departmentRepositoryPort.existsByName(name)) {
            throw new DepartmentAlreadyExistsException(name);
        }
        department.setName(name);
        Department saved = departmentRepositoryPort.save(department);

        // Invalidar caché para evitar datos desactualizados
        evictDepartmentsCache();

        return saved;
    }

    @Override
    public List<Department> getAllDepartments() {
        return getAllDepartmentsFromCache();
    }

    @SuppressWarnings("unchecked")
    public List<Department> getAllDepartmentsFromCache() {
        // completo
        try {
            // 1. Intento de lectura desde la caché de Redis (Cache Hit)
            Object cachedData = redisTemplate.opsForValue().get(DEPARTMENTS_CACHE_KEY);
            if (cachedData instanceof List<?> list && !list.isEmpty()) {
                log.info("Redis Cache HIT: Departamentos recuperados desde la caché");
                return (List<Department>) cachedData;
            }
        } catch (Exception e) {
            log.error("Error al consultar la caché de Redis, fallback a base de datos: {}", e.getMessage());
        }

        // 2. Cache Miss o error de conexión: consultar base de datos
        log.info("Redis Cache MISS: Consultando base de datos para departamentos");
        List<Department> departments = departmentRepositoryPort.findAll();

        // 3. Guardar el resultado en Redis con tiempo de vida (TTL)
        try {
            if (departments != null && !departments.isEmpty()) {
                redisTemplate.opsForValue().set(DEPARTMENTS_CACHE_KEY, departments, CACHE_TTL);
                log.info("Redis Cache UPDATED: Departamentos almacenados en caché con TTL de {}", CACHE_TTL);
            }
        } catch (Exception e) {
            log.error("Error al actualizar la caché de Redis: {}", e.getMessage());
        }

        return departments;
    }

    private void evictDepartmentsCache() {
        try {
            Boolean deleted = redisTemplate.delete(DEPARTMENTS_CACHE_KEY);
            log.info("Redis Cache EVICTED: Clave '{}' eliminada: {}", DEPARTMENTS_CACHE_KEY, deleted);
        } catch (Exception e) {
            log.error("Error al invalidar la caché de Redis: {}", e.getMessage());
        }
    }

    @Override
    public Department getDepartmentById(UUID id) {
        return departmentRepositoryPort.findById(id).orElseThrow(() -> new DepartmentNotFoundException(id));
    }

    @Override
    @Transactional
    public Department update(UUID id, Department department) {
        Department existingDepartment = departmentRepositoryPort.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
        if (department.getName() == null || department.getName().trim().isEmpty()) {
            throw new BusinessException("Department name cannot be empty");
        }
        var name = department.getName().trim();
        if (departmentRepositoryPort.existsByNameAndIdNot(name, id)) {
            throw new DepartmentAlreadyExistsException(name);
        }
        existingDepartment.setName(name);
        existingDepartment.setActive(department.getActive());
        Department updated = departmentRepositoryPort.save(existingDepartment);
        evictDepartmentsCache();
        return updated;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!departmentRepositoryPort.existsById(id)) {
            throw new DepartmentNotFoundException(id);
        }
        departmentRepositoryPort.delete(id);
        evictDepartmentsCache();
    }

    @Override
    public PageResult<Department> execute(PageQuery pageQuery) {
        log.info("es: {}", pageQuery);
        return getDepartmentsFromCache(pageQuery);
    }

    @SuppressWarnings("unchecked")
    public PageResult<Department> getDepartmentsFromCache(PageQuery pageQuery) {
        // 1. Crear una clave dinámica según la página y tamaño
        String cacheKey = DEPARTMENTS_CACHE_KEY + ":page:" + pageQuery.page() + ":size:" + pageQuery.size();
        try {
            // 1. Intento de lectura desde la caché de Redis (Cache Hit)
            Object cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData instanceof PageResult<?> cachedPage && !cachedPage.content().isEmpty()) {
                log.info("Redis Cache HIT: Departamentos recuperados desde la caché key: {} ", cacheKey);
                return (PageResult<Department>) cachedPage;
            }
        } catch (Exception e) {
            log.error("Error al consultar la caché de Redis, fallback a base de datos: {}", e.getMessage());
        }

        log.info("Redis Cache MISS: Consultando base de datos para categories paginados");
        PageResult<Department> pageResult = departmentRepositoryPort.findAllPages(pageQuery);

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
