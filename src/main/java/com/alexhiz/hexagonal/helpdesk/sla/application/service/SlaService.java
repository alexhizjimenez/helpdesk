package com.alexhiz.hexagonal.helpdesk.sla.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alexhiz.hexagonal.helpdesk.sla.application.port.in.CreateSlaUseCase;
import com.alexhiz.hexagonal.helpdesk.sla.application.port.in.DeleteSlaUseCase;
import com.alexhiz.hexagonal.helpdesk.sla.application.port.in.GetSlaByIdUseCase;
import com.alexhiz.hexagonal.helpdesk.sla.application.port.in.ListSlasUseCase;
import com.alexhiz.hexagonal.helpdesk.sla.application.port.in.UpdateSlaUseCase;
import com.alexhiz.hexagonal.helpdesk.sla.application.port.out.SlaRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.sla.domain.exception.SlaAlreadyExistException;
import com.alexhiz.hexagonal.helpdesk.sla.domain.exception.SlaNotFoundException;
import com.alexhiz.hexagonal.helpdesk.sla.domain.model.Sla;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlaService
        implements CreateSlaUseCase, GetSlaByIdUseCase, ListSlasUseCase, DeleteSlaUseCase, UpdateSlaUseCase {
    private final SlaRepositoryPort slaRepositoryPort;

    @Override
    @Transactional
    public Sla create(Sla sla) {
        var name = sla.getName().trim();
        if (slaRepositoryPort.existsByName(name)) {
            throw new SlaAlreadyExistException(name);
        }
        sla.setName(name);
        return slaRepositoryPort.save(sla);
    }

    @Override
    @Transactional
    public Sla update(UUID id, Sla sla) {
        var name = sla.getName().trim();
        if (slaRepositoryPort.existsByNameAndIdNot(name, id)) {
            throw new SlaAlreadyExistException(name);
        }

        Sla updatingSla = slaRepositoryPort.getById(id).orElseThrow(() -> new SlaNotFoundException(id));

        updatingSla.setName(name);
        updatingSla.setPriority(sla.getPriority());
        updatingSla.setResolutionTimeMinutes(sla.getResolutionTimeMinutes());
        updatingSla.setResponseTimeMinutes(sla.getResponseTimeMinutes());
        updatingSla.setActive(sla.getActive());

        return slaRepositoryPort.save(updatingSla);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!slaRepositoryPort.existsById(id)) {
            throw new SlaNotFoundException(id);
        }
        slaRepositoryPort.delete(id);
    }

    @Override
    public List<Sla> getAllSlas() {
        return slaRepositoryPort.findAll();
    }

    @Override
    public Sla getById(UUID id) {
        return slaRepositoryPort.getById(id).orElseThrow(() -> new SlaNotFoundException(id));
    }

}
