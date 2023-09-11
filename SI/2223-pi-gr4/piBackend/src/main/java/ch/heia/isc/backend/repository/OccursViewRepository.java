package ch.heia.isc.backend.repository;

import ch.heia.isc.backend.model.OccursEntityPK;
import ch.heia.isc.backend.model.OccursViewEntity;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@org.springframework.stereotype.Repository
public interface OccursViewRepository extends CrudRepository<OccursViewEntity, LocalDate> {
    @NotNull
    List<OccursViewEntity> findAll();

    // Find all activities ordered by date
    @NotNull
    List<OccursViewEntity> findAllByOrderByDateActivityAsc();

    // Find activity with the PK (idActivity, dateActivity)
    OccursViewEntity findByIdAndDateActivity(String idActivity, LocalDate dateActivity);
}
