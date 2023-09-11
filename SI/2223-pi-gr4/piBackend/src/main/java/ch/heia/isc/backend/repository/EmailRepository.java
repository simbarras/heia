package ch.heia.isc.backend.repository;

import ch.heia.isc.backend.model.EmailEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface EmailRepository extends CrudRepository<EmailEntity, String> {
    List<EmailEntity> findAll();
}
