package com.alexhiz.hexagonal.helpdesk.role.application.service;

import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.role.application.port.in.*;
import com.alexhiz.hexagonal.helpdesk.role.application.port.out.RoleRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.role.domain.exception.RoleAlreadyExistsException;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.core.support.RepositoryMethodInvocationListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService implements CreateRoleUseCase, GetRoleByIdUseCase, UpdateRoleUseCase, ListRoleUseCase, DeleteRoleUseCase {

    private final RoleRepositoryPort roleRepositoryPort;
    private static final String ROLES_CACHE_KEY = "roles";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Role create(Role role) {
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            throw new BusinessException("Role name cannot be empty");
        }

        if (roleRepositoryPort.existsByName(role.getName().trim())) {
            throw new RoleAlreadyExistsException(role.getName().trim());
        }
        evictRolesCache();
        return roleRepositoryPort.save(role);
    }

    @Override
    public void delete(UUID id) {
        if( !roleRepositoryPort.existsById(id)){
            throw  new BusinessException("Role not found: " + id);
        }
        roleRepositoryPort.delete(id);
        evictRolesCache();
    }

    @Override
    public Optional<Role> getRoleById(UUID id) {
        return roleRepositoryPort.findById(id);
    }

    @Override
    public List<Role> listRole() {
        return getRolesFromCache();
    }

    @Override
    public Role updateRole(UUID id, Role role) {
        Role existingRole =  roleRepositoryPort.findById(id).orElseThrow(() -> new BusinessException("Role not found with id: " + id));
        existingRole.setName(role.getName());
        Role updated = roleRepositoryPort.save(existingRole);
        evictRolesCache();
        return updated;
    }

    private void evictRolesCache() {
        try {
            Boolean deleted = redisTemplate.delete(ROLES_CACHE_KEY);
            log.info("Redis Cache EVICTED: Clave '{}' eliminada: {}", ROLES_CACHE_KEY, deleted);
        } catch (Exception e) {
            log.error("Error al invalidar la caché de Redis: {}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Role> getRolesFromCache() {
        try {
            // 1. Intento de lectura desde la caché de Redis (Cache Hit)
            Object cachedData = redisTemplate.opsForValue().get(ROLES_CACHE_KEY);
            if (cachedData instanceof List<?> list && !list.isEmpty()) {
                log.info("Redis Cache HIT: Departamentos recuperados desde la caché");
                return (List<Role>) cachedData;
            }
        } catch (Exception e) {
            log.error("Error al consultar la caché de Redis, fallback a base de datos: {}", e.getMessage());
        }

        // 2. Cache Miss o error de conexión: consultar base de datos
        log.info("Redis Cache MISS: Consultando base de datos para departamentos");
        List<Role> roles = roleRepositoryPort.findAll();

        // 3. Guardar el resultado en Redis con tiempo de vida (TTL)
        try {
            if (roles != null && !roles.isEmpty()) {
                redisTemplate.opsForValue().set(ROLES_CACHE_KEY, roles, CACHE_TTL);
                log.info("Redis Cache UPDATED: Roles almacenados en caché con TTL de {}", CACHE_TTL);
            }
        } catch (Exception e) {
            log.error("Error al actualizar la caché de Redis: {}", e.getMessage());
        }

        return roles;
    }
}
