package com.alexhiz.hexagonal.helpdesk.shared.domain.model;

public record PageQuery(int page, int size) {
    public PageQuery {
        if (page < 0) throw new IllegalArgumentException("La página no puede ser menor a 0");
        if (size <= 0) throw new IllegalArgumentException("El tamaño debe ser mayor a 0");
    }
}