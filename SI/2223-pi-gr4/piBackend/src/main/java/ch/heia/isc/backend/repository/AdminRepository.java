package ch.heia.isc.backend.repository;

import ch.heia.isc.backend.model.AdminEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends CrudRepository<AdminEntity, String> {
    List<AdminEntity> findAll();

    Optional<AdminEntity> findByEmail(String email);
}
