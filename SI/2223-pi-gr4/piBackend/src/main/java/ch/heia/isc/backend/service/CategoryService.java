package ch.heia.isc.backend.service;

import ch.heia.isc.backend.model.CategoryEntity;

import ch.heia.isc.backend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

@Service
@ApplicationScope
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Get all categories
     * @return List of categories
     */
    public List<CategoryEntity> getCategories() {
        return categoryRepository.findAll();
    }

}
