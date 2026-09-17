package com.alexhiz.hexagonal.helpdesk.sla.application.port.in;

import java.util.UUID;

public interface DeleteSlaUseCase {
    void delete(UUID id);
}
