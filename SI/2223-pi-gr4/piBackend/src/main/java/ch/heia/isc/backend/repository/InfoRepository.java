package ch.heia.isc.backend.repository;

import ch.heia.isc.backend.model.InfoEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface InfoRepository extends CrudRepository<InfoEntity, String> {
    List<InfoEntity> findAll();
}
