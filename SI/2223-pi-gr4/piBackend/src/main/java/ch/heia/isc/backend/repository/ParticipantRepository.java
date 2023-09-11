package ch.heia.isc.backend.repository;

import ch.heia.isc.backend.model.ParticipantEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipantRepository extends CrudRepository<ParticipantEntity, String> {
    Optional<ParticipantEntity> findByEmail(String email);
}
