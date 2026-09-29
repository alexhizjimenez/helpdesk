package com.alexhiz.hexagonal.helpdesk.role.domain.model;

public enum PermissionEnum {
    // --- PERMISOS DE USER ---
    // Crear tickets de soporte
    TICKET_CREATE,

    // Consultar únicamente los tickets creados por el propio usuario
    TICKET_READ_OWN,

    // Agregar comentarios a sus tickets
    TICKET_COMMENT_ADD,

    // Cerrar únicamente los tickets creados por el propio usuario
    TICKET_CLOSE_OWN,

    // Reabrir un ticket cerrado previamente por el usuario
    TICKET_REOPEN,

    // --- PERMISOS DE AGENT ---
    // Consultar los tickets que le han sido asignados
    TICKET_READ_ASSIGNED,

    // Tomar/asignarse un ticket pendiente a sí mismo
    TICKET_CLAIM,

    // Cambiar el estado actual de un ticket
    TICKET_STATUS_UPDATE,

    // Marcar un ticket como resuelto
    TICKET_RESOLVE,

    // --- PERMISOS DE SUPERVISOR ---
    // Asignar tickets a otros agentes
    TICKET_ASSIGN,

    // Reasignar un ticket a un agente o departamento diferente
    TICKET_REASSIGN,

    // Consultar todos los tickets pertenecientes a su equipo o área
    TICKET_READ_TEAM,

    // Consultar reportes y métricas del equipo o del sistema
    METRICS_READ,

    // Modificar o gestionar el nivel de prioridad de los tickets
    PRIORITY_MANAGE,

    // --- PERMISOS DE ADMIN ---
    // Administrar usuarios (crear, editar, eliminar, activar)
    USER_MANAGE,

    // Administrar perfiles y asignaciones de agentes
    AGENT_MANAGE,

    // Crear, editar y eliminar categorías de tickets
    CATEGORY_MANAGE,

    // Crear y organizar departamentos o áreas de atención
    DEPARTMENT_MANAGE,

    // Configurar y gestionar acuerdos de nivel de servicio (SLA)
    SLA_MANAGE,

    // Consultar historiales y registros de auditoría del sistema
    AUDIT_READ
}
