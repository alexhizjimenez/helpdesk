package com.alexhiz.hexagonal.helpdesk.department.testdata;

import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest.dto.DepartmentRequest;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest.dto.DepartmentResponse;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.out.persistence.DepartmentEntity;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Object Mother Pattern para generar datos de prueba consistentes y reutilizables
 * en todos los tests unitarios del módulo de departamentos.
 */
public class DepartmentMother {

    // Fecha fija de referencia para mantener consistencia en las pruebas
    public static final LocalDateTime FIXED_DATE = LocalDateTime.of(2026, 1, 1, 10, 0);

    // Constructor privado para evitar instanciación (clase utilitaria)
    private DepartmentMother() {}

    // ==========================================
    // Fabricas para Modelo de Dominio (Department)
    // ==========================================

    /**
     * Retorna un builder preconfigurado con valores por defecto válidos.
     */
    public static Department.DepartmentBuilder aDepartmentBuilder() {
        return Department.builder()
                .id(1L)
                .publicId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .name("IT")
                .active(true)
                .createdAt(FIXED_DATE)
                .updatedAt(FIXED_DATE);
    }

    /**
     * Retorna una instancia de Department con valores por defecto.
     */
    public static Department aDepartment() {
        return aDepartmentBuilder().build();
    }

    /**
     * Retorna una instancia de Department con un nombre personalizado.
     */
    public static Department aDepartmentNamed(String name) {
        return aDepartmentBuilder().name(name).build();
    }

    /**
     * Retorna una instancia de Department con un UUID y nombre específicos.
     */
    public static Department aDepartmentWith(UUID publicId, String name) {
        return aDepartmentBuilder().publicId(publicId).name(name).build();
    }

    /**
     * Retorna un Department nuevo (sin id ni publicId asignados aún).
     */
    public static Department aNewDepartment(String name) {
        return aDepartmentBuilder().id(null).publicId(null).name(name).build();
    }

    // ==========================================
    // Fabricas para Entidades de Persistencia (DepartmentEntity)
    // ==========================================

    /**
     * Retorna un builder preconfigurado para DepartmentEntity.
     */
    public static DepartmentEntity.DepartmentEntityBuilder aDepartmentEntityBuilder() {
        return DepartmentEntity.builder()
                .id(1L)
                .publicId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .name("IT")
                .active(true)
                .createdAt(FIXED_DATE)
                .updatedAt(FIXED_DATE)
                .deletedAt(null);
    }

    /**
     * Retorna una entidad DepartmentEntity con valores por defecto.
     */
    public static DepartmentEntity aDepartmentEntity() {
        return aDepartmentEntityBuilder().build();
    }

    /**
     * Retorna una entidad DepartmentEntity eliminada lógicamente (soft deleted).
     */
    public static DepartmentEntity aDeletedDepartmentEntity() {
        return aDepartmentEntityBuilder()
                .active(false)
                .deletedAt(FIXED_DATE.plusDays(1))
                .build();
    }

    // ==========================================
    // Fabricas para DTOs (Request / Response)
    // ==========================================

    /**
     * Retorna un DepartmentRequest válido con nombre y estado activo.
     */
    public static DepartmentRequest aDepartmentRequest(String name, Boolean active) {
        return new DepartmentRequest(name, active);
    }

    /**
     * Retorna un DepartmentRequest por defecto ("IT", true).
     */
    public static DepartmentRequest aDefaultDepartmentRequest() {
        return aDepartmentRequest("IT", true);
    }

    /**
     * Retorna un DepartmentResponse basado en un Department de dominio.
     */
    public static DepartmentResponse aDepartmentResponse() {
        return DepartmentResponse.from(aDepartment());
    }
}
