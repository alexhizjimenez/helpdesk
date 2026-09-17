package com.alexhiz.hexagonal.helpdesk.sla.application.service;

import com.alexhiz.hexagonal.helpdesk.sla.application.port.out.SlaRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.sla.domain.exception.SlaAlreadyExistException;
import com.alexhiz.hexagonal.helpdesk.sla.domain.exception.SlaNotFoundException;
import com.alexhiz.hexagonal.helpdesk.sla.domain.model.Sla;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlaServiceTest {

    @Mock
    private SlaRepositoryPort slaRepositoryPort;

    @InjectMocks
    private SlaService slaService;

    @Test
    void shouldUpdateSlaSuccessfully() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Sla existingSla = Sla.builder()
                .id(id)
                .name("SLA Antiguo")
                .priority("LOW")
                .responseTimeMinutes("60")
                .resolutionTimeMinutes("240")
                .active("true")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Sla updateData = Sla.builder()
                .name("  SLA Actualizado  ")
                .priority("HIGH")
                .responseTimeMinutes("15")
                .resolutionTimeMinutes("60")
                .active("false")
                .build();

        when(slaRepositoryPort.existsByNameAndIdNot("SLA Actualizado", id)).thenReturn(false);
        when(slaRepositoryPort.getById(id)).thenReturn(Optional.of(existingSla));
        when(slaRepositoryPort.save(any(Sla.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Sla result = slaService.update(id, updateData);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("SLA Actualizado");
        assertThat(result.getPriority()).isEqualTo("HIGH");
        assertThat(result.getResponseTimeMinutes()).isEqualTo("15");
        assertThat(result.getResolutionTimeMinutes()).isEqualTo("60");
        assertThat(result.getActive()).isEqualTo("false");

        verify(slaRepositoryPort).existsByNameAndIdNot("SLA Actualizado", id);
        verify(slaRepositoryPort).save(existingSla);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithDuplicateName() {
        UUID id = UUID.randomUUID();
        Sla updateData = Sla.builder()
                .name("SLA Existente")
                .build();

        when(slaRepositoryPort.existsByNameAndIdNot("SLA Existente", id)).thenReturn(true);

        assertThatThrownBy(() -> slaService.update(id, updateData))
                .isInstanceOf(SlaAlreadyExistException.class);

        verify(slaRepositoryPort, never()).getById(any());
        verify(slaRepositoryPort, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentSla() {
        UUID id = UUID.randomUUID();
        Sla updateData = Sla.builder()
                .name("SLA Nuevo")
                .build();

        when(slaRepositoryPort.existsByNameAndIdNot("SLA Nuevo", id)).thenReturn(false);
        when(slaRepositoryPort.getById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> slaService.update(id, updateData))
                .isInstanceOf(SlaNotFoundException.class);

        verify(slaRepositoryPort, never()).save(any());
    }

    @Test
    void shouldCreateSlaSuccessfully() {
        Sla newSla = Sla.builder()
                .name("  SLA Normal  ")
                .priority("MEDIUM")
                .responseTimeMinutes("30")
                .resolutionTimeMinutes("120")
                .build();

        when(slaRepositoryPort.existsByName("SLA Normal")).thenReturn(false);
        when(slaRepositoryPort.save(any(Sla.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Sla result = slaService.create(newSla);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("SLA Normal");
        verify(slaRepositoryPort).save(newSla);
    }

    @Test
    void shouldThrowExceptionWhenCreatingWithDuplicateName() {
        Sla newSla = Sla.builder()
                .name("SLA Duplicado")
                .build();

        when(slaRepositoryPort.existsByName("SLA Duplicado")).thenReturn(true);

        assertThatThrownBy(() -> slaService.create(newSla))
                .isInstanceOf(SlaAlreadyExistException.class);

        verify(slaRepositoryPort, never()).save(any());
    }

    @Test
    void shouldDeleteSlaSuccessfully() {
        UUID id = UUID.randomUUID();
        when(slaRepositoryPort.existsById(id)).thenReturn(true);

        slaService.delete(id);

        verify(slaRepositoryPort).delete(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentSla() {
        UUID id = UUID.randomUUID();
        when(slaRepositoryPort.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> slaService.delete(id))
                .isInstanceOf(SlaNotFoundException.class);

        verify(slaRepositoryPort, never()).delete(any());
    }

    @Test
    void shouldGetAllSlas() {
        List<Sla> slas = List.of(Sla.builder().id(UUID.randomUUID()).name("SLA 1").build());
        when(slaRepositoryPort.findAll()).thenReturn(slas);

        List<Sla> result = slaService.getAllSlas();

        assertThat(result).hasSize(1);
        verify(slaRepositoryPort).findAll();
    }

    @Test
    void shouldGetSlaById() {
        UUID id = UUID.randomUUID();
        Sla sla = Sla.builder().id(id).name("SLA 1").build();
        when(slaRepositoryPort.getById(id)).thenReturn(Optional.of(sla));

        Sla result = slaService.getById(id);

        assertThat(result).isEqualTo(sla);
    }
}
