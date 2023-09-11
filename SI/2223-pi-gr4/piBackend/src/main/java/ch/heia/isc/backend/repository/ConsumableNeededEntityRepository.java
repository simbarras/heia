package ch.heia.isc.backend.repository;

import ch.heia.isc.backend.model.ConsumableNeededEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface ConsumableNeededEntityRepository extends CrudRepository<ConsumableNeededEntity, String> {
    List<ConsumableNeededEntity> findAll();

    List<ConsumableNeededEntity> findAllByActivity(String activity);


    // Delete all consumable needed by activity
    @Transactional
    void deleteAllByActivity(String activity);
}
