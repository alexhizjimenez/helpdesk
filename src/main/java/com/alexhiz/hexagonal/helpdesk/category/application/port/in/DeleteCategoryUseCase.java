package com.alexhiz.hexagonal.helpdesk.category.application.port.in;

import java.util.UUID;

public interface DeleteCategoryUseCase {
    void delete(UUID id);
}
