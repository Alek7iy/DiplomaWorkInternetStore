package Service;

import DTO.CategoryRequest;
import Repository.CategoryRepository;
import entity.Category;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import Exception.BusinessException;
import Exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(CategoryRequest request){
        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        category.setStatus(request.status() != null? request.status() : Category.Status.ACTIVE);

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getProducts().isEmpty()) {
            throw new BusinessException("Cannot delete category with products. Remove products first.");
        }

        categoryRepository.delete(category);
    }

    public Category updateCategory(Long categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        category.setName(request.name());
        category.setDescription(request.description());
        category.setStatus(request.status());

        return categoryRepository.save(category);
    }
}
