package ch.heia.isc.backend.controller;

import ch.heia.isc.backend.model.CategoryEntity;
import ch.heia.isc.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class CategoryController {
    private final String BASE_URL = "/api/v1";
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Get all categories
     * @return List of categories
     */
    @GetMapping(path = BASE_URL + "/categories", produces = "application/json")
    public List<CategoryEntity> activities() {
        return categoryService.getCategories();
    }
}
