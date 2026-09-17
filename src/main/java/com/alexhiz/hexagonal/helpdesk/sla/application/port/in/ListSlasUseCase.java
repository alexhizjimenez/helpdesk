package com.alexhiz.hexagonal.helpdesk.sla.application.port.in;

import java.util.List;

import com.alexhiz.hexagonal.helpdesk.sla.domain.model.Sla;

public interface ListSlasUseCase {
    List<Sla> getAllSlas();
}
