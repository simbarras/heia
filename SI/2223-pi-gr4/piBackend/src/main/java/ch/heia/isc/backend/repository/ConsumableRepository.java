package ch.heia.isc.backend.repository;

import ch.heia.isc.backend.model.ConsumableEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface ConsumableRepository extends CrudRepository<ConsumableEntity, String> {
    List<ConsumableEntity> findAll();
}
