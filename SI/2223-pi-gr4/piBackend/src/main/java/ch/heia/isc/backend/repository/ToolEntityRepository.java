package ch.heia.isc.backend.repository;

import ch.heia.isc.backend.model.ToolEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface ToolEntityRepository extends CrudRepository<ToolEntity, String> {
    List<ToolEntity> findAll();
}
