package com.alexhiz.hexagonal.helpdesk.department.application.port.in;

import java.util.UUID;

public interface DeleteDepartmentUseCase {
    void deleteDepartmentById(UUID id);
}
