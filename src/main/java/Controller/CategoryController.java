package Controller;

import DTO.CategoryRequest;
import DTO.CategoryResponse;
import Repository.CategoryRepository;
import Service.CategoryService;
import entity.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Управление категориями")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать категорию")
    public ResponseEntity<CategoryResponse> create(@RequestBody @Valid CategoryRequest request) {
        Category category = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(category));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить категорию")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Получить активные категории")
    public ResponseEntity<List<CategoryResponse>> getActiveCategories() {
        List<Category> categories = categoryRepository.findAllByStatus(Category.Status.ACTIVE);
        return ResponseEntity.ok(categories.stream()
                .map(this::toResponse)
                .toList());
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getStatus()
        );
    }
}


