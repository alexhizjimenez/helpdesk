package com.alexhiz.hexagonal.helpdesk.role.application.service;

import com.alexhiz.hexagonal.helpdesk.role.application.port.in.CreateRoleUseCase;
import com.alexhiz.hexagonal.helpdesk.role.application.port.out.RoleRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.role.domain.exception.RoleAlreadyExistsException;
import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService implements CreateRoleUseCase {

    private final RoleRepositoryPort roleRepositoryPort;

    @Override
    public Role create(Role role) {
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            throw new BusinessException("Role name cannot be empty");
        }

        if (roleRepositoryPort.existsByName(role.getName().trim())) {
            throw new RoleAlreadyExistsException(role.getName().trim());
        }

        return roleRepositoryPort.save(role);
    }
}
