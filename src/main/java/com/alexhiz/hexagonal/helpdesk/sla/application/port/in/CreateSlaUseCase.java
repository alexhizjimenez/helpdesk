package com.alexhiz.hexagonal.helpdesk.sla.application.port.in;

import com.alexhiz.hexagonal.helpdesk.sla.domain.model.Sla;

public interface CreateSlaUseCase {
    Sla create(Sla sla);

}
