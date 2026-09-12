package com.alexhiz.hexagonal.helpdesk.user.application.service;

import com.alexhiz.hexagonal.helpdesk.department.application.port.out.DepartmentRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.department.domain.exception.DepartmentNotFoundException;
import com.alexhiz.hexagonal.helpdesk.role.application.port.out.RoleRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.role.domain.exception.RoleNotFoundException;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.BusinessException;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import com.alexhiz.hexagonal.helpdesk.user.application.port.in.CreateUserUseCase;
import com.alexhiz.hexagonal.helpdesk.user.application.port.in.DeleteUserUseCase;
import com.alexhiz.hexagonal.helpdesk.user.application.port.in.GetUserByIdUseCase;
import com.alexhiz.hexagonal.helpdesk.user.application.port.in.PageUsersUseCase;
import com.alexhiz.hexagonal.helpdesk.user.application.port.in.UpdateUserUseCase;
import com.alexhiz.hexagonal.helpdesk.user.application.port.out.UserRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.user.domain.exception.UserAlreadyExistsException;
import com.alexhiz.hexagonal.helpdesk.user.domain.exception.UserNotFoundException;
import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements CreateUserUseCase, UpdateUserUseCase, DeleteUserUseCase, GetUserByIdUseCase, PageUsersUseCase {
    private static final String USER_CACHE_KEY = "users_key_cache";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);


    private final UserRepositoryPort userRepositoryPort;
    private final DepartmentRepositoryPort departmentRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;

    private final RedisTemplate<String, Object> redisTemplate;


    @Override
    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new BusinessException("User email cannot be empty");
        }

        String email = user.getEmail().trim().toLowerCase();
        user.setEmail(email);

        if (userRepositoryPort.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }

        if (user.getDepartmentId() != null) {
            if (!departmentRepositoryPort.existsById(user.getDepartmentId())) {
                throw new DepartmentNotFoundException(user.getDepartmentId());
            }
        }

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<Role> resolvedRoles = new HashSet<>();
            for (Role role : user.getRoles()) {
                Role foundRole = roleRepositoryPort.findById(role.getId())
                        .orElseThrow(() -> new RoleNotFoundException(role.getId()));
                resolvedRoles.add(foundRole);
            }
            user.setRoles(resolvedRoles);
        }

        return userRepositoryPort.save(user);
    }

    @Override
    public PageResult<User> execute(PageQuery pageQuery) {
        return getAllUserPageFromCache(pageQuery);
    }

    @Override
    public User getUserById(UUID id) {
        return userRepositoryPort.findById(id).orElseThrow(()-> new UserNotFoundException(id));
    }

    @Override
    public void delete(UUID id) {
        if(!userRepositoryPort.existsById(id)){
            throw new UserNotFoundException(id);
        }
        userRepositoryPort.delete(id);
    }

    @Override
    public User update(UUID id, User user) {
        User updateUser = userRepositoryPort.findById(id).orElseThrow(()-> new UserNotFoundException(id));
        var email = user.getEmail();
        var departmentId = user.getDepartmentId();

        if(userRepositoryPort.existsByEmailAndIdNot(email, id)){
            throw new UserAlreadyExistsException(email);
        }
        if(!departmentRepositoryPort.existsById(departmentId)){
            throw new DepartmentNotFoundException(departmentId);
        }

        updateUser.setFullName(user.getFullName());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setDepartmentId(user.getDepartmentId());
        User saved = userRepositoryPort.save(updateUser);
        return saved;
    }

    public PageResult<User> getAllUserPageFromCache (PageQuery pageQuery){
        String cacheKey = USER_CACHE_KEY+":page:"+pageQuery.page()+":size:"+pageQuery.size();

        try {
            Object cacheData = redisTemplate.opsForValue().get(cacheKey);
            if(cacheData instanceof PageResult<?> cachePage  &&  !cachePage.content().isEmpty()) {
                log.info("Redis Cache HIT: Usuarios recuperados desde la caché para key: {}", cacheKey);
                return (PageResult<User>) cachePage;
            }
        } catch (Exception e) {
            log.error("Error al consultar la caché de Redis, fallback a base de datos: {}", e.getMessage());
        }

        log.info("Redis Cache MISS: Consultando base de datos para users paginados");

        PageResult<User> pageResult = userRepositoryPort.findAllPages(pageQuery);
        try {
            if(pageResult != null && !pageResult.content().isEmpty()){
                redisTemplate.opsForValue().set(cacheKey,pageResult,CACHE_TTL);
            }
        } catch (Exception e) {
            log.error("Error al actualizar la caché de Redis: {}", e.getMessage());
        }

        return pageResult;
    }
}
