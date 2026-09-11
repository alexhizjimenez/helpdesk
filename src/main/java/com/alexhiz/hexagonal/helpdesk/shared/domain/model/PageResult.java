package com.alexhiz.hexagonal.helpdesk.shared.domain.model;

import java.util.List;
import java.util.function.Function;

public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public <R> PageResult<R> map(Function<T, R> converter) {
        List<R> mappedContent = (this.content == null || converter == null)
                ? List.of()
                : this.content.stream()
                .map(converter)
                .toList();
        return new PageResult<>(mappedContent, this.page, this.size, this.totalElements, this.totalPages);
    }
}
