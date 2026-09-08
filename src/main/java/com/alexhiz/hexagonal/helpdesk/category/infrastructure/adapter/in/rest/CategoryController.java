package com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.category.application.port.in.*;
import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.in.rest.dto.CategoryRequest;
import com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.in.rest.dto.CategoryResponse;
import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private  final CreateCategoryUseCase createCategoryUseCase;
    private  final ListCategoryUseCase listCategoryUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;


    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request){
        Category category = createCategoryUseCase.create(request.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryResponse.from(category));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll(){
        List<CategoryResponse>  list = listCategoryUseCase.getAllCategory().stream().map(CategoryResponse::from).toList();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(list);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<CategoryResponse> findById(@PathVariable UUID id){
        Category category = getCategoryByIdUseCase.getCategoryById(id).orElseThrow(() -> new BusinessException("Category not found"+id));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(CategoryResponse.from(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request){
        Category category = updateCategoryUseCase.update(id, request.toDomain());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(CategoryResponse.from(category));
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<Void> delete(@PathVariable UUID id){
        deleteCategoryUseCase.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



}
