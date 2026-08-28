package com.alexhiz.hexagonal.helpdesk.department.application.service;

import com.alexhiz.hexagonal.helpdesk.department.application.port.in.CreateDepartmentUseCase;
import com.alexhiz.hexagonal.helpdesk.department.application.port.in.DeleteDepartmentUseCase;
import com.alexhiz.hexagonal.helpdesk.department.application.port.in.GetDepartmentByIdUseCase;
import com.alexhiz.hexagonal.helpdesk.department.application.port.in.ListDepartmentsUseCase;
import com.alexhiz.hexagonal.helpdesk.department.application.port.in.UpdateDepartmentUseCase;
import com.alexhiz.hexagonal.helpdesk.department.application.port.out.DepartmentRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.department.domain.exception.DepartmentAlreadyExistsException;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentService implements CreateDepartmentUseCase, ListDepartmentsUseCase, GetDepartmentByIdUseCase, UpdateDepartmentUseCase, DeleteDepartmentUseCase {

    private static final String DEPARTMENTS_CACHE_KEY = "departments";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final DepartmentRepositoryPort departmentRepositoryPort;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Department create(Department department) {
        if (department.getName() == null || department.getName().trim().isEmpty()) {
            throw new BusinessException("Department name cannot be empty");
        } 

        if (departmentRepositoryPort.existsByName(department.getName().trim())) {
            throw new DepartmentAlreadyExistsException(department.getName().trim());
        }
        
        Department saved = departmentRepositoryPort.save(department);

        // Invalidar caché para evitar datos desactualizados
        evictDepartmentsCache();

        return saved;
    }

    @Override
    public List<Department> listDepartments() {
        return getDepartmentsFromCache();
    }

    @SuppressWarnings("unchecked")
    public List<Department> getDepartmentsFromCache() {
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
    public Optional<Department> getDepartmentById(UUID id) {
        return departmentRepositoryPort.findById(id);
    }

    @Override
    public Department updateDepartment(UUID id, Department department) {
        Department existingDepartment = departmentRepositoryPort.findById(id)
                .orElseThrow(() -> new BusinessException("Department not found with id: " + id));
        existingDepartment.setName(department.getName());
        existingDepartment.setActive(department.getActive());
        Department updated = departmentRepositoryPort.save(existingDepartment);
        evictDepartmentsCache();
        return updated;
    }

    @Override
    public void deleteDepartmentById(UUID id) {
        if (!departmentRepositoryPort.existsById(id)) {
            throw new BusinessException("Department not found with id: " + id);
        }
        departmentRepositoryPort.delete(id);
        evictDepartmentsCache();
    }
}

