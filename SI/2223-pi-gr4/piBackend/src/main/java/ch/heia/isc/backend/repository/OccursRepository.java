package ch.heia.isc.backend.repository;

import ch.heia.isc.backend.model.OccursEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface OccursRepository extends CrudRepository<OccursEntity, String> {
    List<OccursEntity> findAll();

    List<OccursEntity> findAllByIdActivity(String idActivity);

}
