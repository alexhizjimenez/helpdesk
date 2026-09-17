package com.alexhiz.hexagonal.helpdesk.sla.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.shared.infrastructure.adapter.in.rest.GlobalExceptionHandler;
import com.alexhiz.hexagonal.helpdesk.sla.application.port.in.CreateSlaUseCase;
import com.alexhiz.hexagonal.helpdesk.sla.application.port.in.UpdateSlaUseCase;
import com.alexhiz.hexagonal.helpdesk.sla.domain.model.Sla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SlaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateSlaUseCase createSlaUseCase;

    @Mock
    private UpdateSlaUseCase updateSlaUseCase;

    @InjectMocks
    private SlaController slaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(slaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldCreateSlaSuccessfullyWhenPayloadIsValid() throws Exception {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Sla createdSla = Sla.builder()
                .id(id)
                .name("SLA Crítico")
                .priority("HIGH")
                .responseTimeMinutes("15")
                .resolutionTimeMinutes("60")
                .active("true")
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(createSlaUseCase.create(any(Sla.class))).thenReturn(createdSla);

        String requestJson = """
                {
                    "name": "SLA Crítico",
                    "priority": "HIGH",
                    "responseTimeMinutes": "15",
                    "resolutionTimeMinutes": "60",
                    "active": "true"
                }
                """;

        mockMvc.perform(post("/api/sla")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("SLA Crítico"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.responseTimeMinutes").value("15"))
                .andExpect(jsonPath("$.resolutionTimeMinutes").value("60"))
                .andExpect(jsonPath("$.active").value("true"));
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        String invalidJson = """
                {
                    "name": "",
                    "priority": "",
                    "responseTimeMinutes": "",
                    "resolutionTimeMinutes": ""
                }
                """;

        mockMvc.perform(post("/api/sla")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.name").value("name is required"))
                .andExpect(jsonPath("$.validationErrors.priority").value("priority is required"))
                .andExpect(jsonPath("$.validationErrors.responseTimeMinutes").value("responseTimeMinutes is required"))
                .andExpect(jsonPath("$.validationErrors.resolutionTimeMinutes").value("resolutionTimeMinutes is required"));
    }

    @Test
    void shouldUpdateSlaSuccessfullyWhenPayloadIsValid() throws Exception {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Sla updatedSla = Sla.builder()
                .id(id)
                .name("SLA Modificado")
                .priority("HIGH")
                .responseTimeMinutes("20")
                .resolutionTimeMinutes("80")
                .active("true")
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(updateSlaUseCase.update(eq(id), any(Sla.class))).thenReturn(updatedSla);

        String requestJson = """
                {
                    "name": "SLA Modificado",
                    "priority": "HIGH",
                    "responseTimeMinutes": "20",
                    "resolutionTimeMinutes": "80",
                    "active": "true"
                }
                """;

        mockMvc.perform(put("/api/sla/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("SLA Modificado"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.responseTimeMinutes").value("20"))
                .andExpect(jsonPath("$.resolutionTimeMinutes").value("80"))
                .andExpect(jsonPath("$.active").value("true"));
    }
}
