package com.alexhiz.hexagonal.helpdesk.user.application.service;

import com.alexhiz.hexagonal.helpdesk.department.application.port.out.DepartmentRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.department.domain.exception.DepartmentNotFoundException;
import com.alexhiz.hexagonal.helpdesk.role.application.port.out.RoleRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.role.domain.exception.RoleNotFoundException;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.BusinessException;
import com.alexhiz.hexagonal.helpdesk.user.application.port.in.CreateUserUseCase;
import com.alexhiz.hexagonal.helpdesk.user.application.port.out.UserRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.user.domain.exception.UserAlreadyExistsException;
import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements CreateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final DepartmentRepositoryPort departmentRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;

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
}
