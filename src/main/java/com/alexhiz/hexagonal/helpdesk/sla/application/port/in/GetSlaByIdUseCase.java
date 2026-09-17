package com.alexhiz.hexagonal.helpdesk.sla.application.port.in;

import java.util.UUID;

import com.alexhiz.hexagonal.helpdesk.sla.domain.model.Sla;

public interface GetSlaByIdUseCase {
    Sla getById(UUID id);
}
