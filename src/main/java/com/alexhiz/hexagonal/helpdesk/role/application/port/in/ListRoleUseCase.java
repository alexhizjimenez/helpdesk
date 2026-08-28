package com.alexhiz.hexagonal.helpdesk.role.application.port.in;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;

import java.util.List;

public interface ListRoleUseCase {
    List<Role> listRole();
}
