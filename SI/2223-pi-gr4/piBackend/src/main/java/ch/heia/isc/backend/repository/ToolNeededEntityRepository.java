package ch.heia.isc.backend.repository;

import ch.heia.isc.backend.model.ToolNeededEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface ToolNeededEntityRepository extends CrudRepository<ToolNeededEntity, String> {
    List<ToolNeededEntity> findAll();

    List<ToolNeededEntity> findAllByActivity(String activity);

    // Delete all tool needed by activity
    @Transactional
    void deleteAllByActivity(String activity);
}
