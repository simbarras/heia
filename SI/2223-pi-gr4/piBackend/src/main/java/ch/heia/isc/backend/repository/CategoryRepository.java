package ch.heia.isc.backend.repository;

import ch.heia.isc.backend.model.CategoryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface CategoryRepository extends CrudRepository<CategoryEntity, String> {
    List<CategoryEntity> findAll();
}
