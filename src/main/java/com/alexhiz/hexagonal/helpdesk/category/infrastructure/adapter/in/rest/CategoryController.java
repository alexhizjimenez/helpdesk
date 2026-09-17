package com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.category.application.port.in.*;
import com.alexhiz.hexagonal.helpdesk.category.domain.model.Category;
import com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.in.rest.dto.CategoryRequest;
import com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.in.rest.dto.CategoryResponse;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Categorías", description = "Operaciones para gestionar las categorías")
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
        @ApiResponse(responseCode = "201", description = "Categoría creada correctamente")
        @ApiResponse(responseCode = "400", description = "Datos de la categoría inválidos")
        @ApiResponse(responseCode = "404", description = "Departamento no encontrado")
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request){
        Category category = createCategoryUseCase.create(request.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryResponse.from(category));
    }

    @GetMapping
    @Operation(summary = "Lista las categorías", description = "Obtiene todas las categorías registradas")
    @ApiResponse(responseCode = "200", description = "Categorías obtenidas correctamente")
    public ResponseEntity<List<CategoryResponse>> findAll(){
        List<CategoryResponse>  list = listCategoriesUseCase.getAllCategories().stream().map(CategoryResponse::from).toList();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una categoría", description = "Busca una categoría por su identificador")
    @ApiResponse(responseCode = "200", description = "Categoría obtenida correctamente")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    public  ResponseEntity<CategoryResponse> findById(@PathVariable UUID id){
        Category category = getCategoryByIdUseCase.getCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(CategoryResponse.from(category));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza una categoría", description = "Actualiza los datos de una categoría existente")
    @ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente")
    @ApiResponse(responseCode = "404", description = "Categoría o departamento no encontrado")
    public ResponseEntity<CategoryResponse> update(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request){
        Category category = updateCategoryUseCase.update(id, request.toDomain());
        return ResponseEntity.status(HttpStatus.OK).body(CategoryResponse.from(category));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una categoría", description = "Elimina una categoría por su identificador")
    @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente")
    @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    public  ResponseEntity<Void> delete(@PathVariable UUID id){
        deleteCategoryUseCase.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/department/{id}")
    @Operation(summary = "Lista categorías por departamento", description = "Obtiene las categorías activas asociadas a un departamento")
    @ApiResponse(responseCode = "200", description = "Categorías obtenidas correctamente")
    @ApiResponse(responseCode = "404", description = "Departamento no encontrado")
    public  ResponseEntity<List<CategoryResponse>> getAllCategoriesByDepartment(@PathVariable UUID id){
        List<CategoryResponse> response = getCategoriesByDepartmentUseCase.getAllCategoriesByDepartment(id).stream().map(CategoryResponse::from).toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/pages")
    @Operation(summary = "Obtiene categorías paginadas", description = "Obtiene una página de categorías usando los parámetros de paginación")
    @ApiResponse(responseCode = "200", description = "Página de categorías obtenida correctamente")
    public ResponseEntity<PageResult<CategoryResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {
        PageResult<CategoryResponse> domainPage = pageCategoriesUseCase.execute(new PageQuery(page, size)).map(CategoryResponse::from);
        return ResponseEntity.status(HttpStatus.OK).body(domainPage);
    }



}
