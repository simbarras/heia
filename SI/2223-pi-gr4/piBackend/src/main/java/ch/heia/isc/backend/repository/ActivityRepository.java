package ch.heia.isc.backend.repository;

import ch.heia.isc.backend.model.ActivityEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends CrudRepository<ActivityEntity, String> {
    List<ActivityEntity> findAll();


}
