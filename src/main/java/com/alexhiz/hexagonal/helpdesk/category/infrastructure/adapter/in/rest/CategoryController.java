package com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.category.application.port.in.*;
import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.in.rest.dto.CategoryRequest;
import com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.in.rest.dto.CategoryResponse;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    private  final ListCategoriesUseCase listCategoriesUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final GetCategoriesByDepartmentUseCase getCategoriesByDepartmentUseCase;
    private final PageCategoriesUseCase pageCategoriesUseCase;

    @Operation(
            summary = "Crea categoria",
            description = "retorna la categoria creada"
    )
    @ApiResponse(responseCode = "200", description = "Categoria creado")
    @ApiResponse(responseCode = "404", description = "Error al crear categoria")
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request){
        Category category = createCategoryUseCase.create(request.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryResponse.from(category));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll(){
        List<CategoryResponse>  list = listCategoriesUseCase.getAllCategories().stream().map(CategoryResponse::from).toList();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<CategoryResponse> findById(@PathVariable UUID id){
        Category category = getCategoryByIdUseCase.getCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(CategoryResponse.from(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request){
        Category category = updateCategoryUseCase.update(id, request.toDomain());
        return ResponseEntity.status(HttpStatus.OK).body(CategoryResponse.from(category));
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<Void> delete(@PathVariable UUID id){
        deleteCategoryUseCase.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/department/{id}")
    public  ResponseEntity<List<CategoryResponse>> getAllCategoriesByDepartment(@PathVariable UUID id){
        List<CategoryResponse> response = getCategoriesByDepartmentUseCase.getAllCategoriesByDepartment(id).stream().map(CategoryResponse::from).toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/pages")
    public ResponseEntity<PageResult<CategoryResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {
        PageResult<CategoryResponse> domainPage = pageCategoriesUseCase.execute(new PageQuery(page, size)).map(CategoryResponse::from);
        return ResponseEntity.status(HttpStatus.OK).body(domainPage);
    }



}
