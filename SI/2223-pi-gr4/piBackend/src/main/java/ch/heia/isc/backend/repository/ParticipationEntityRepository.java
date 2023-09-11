package ch.heia.isc.backend.repository;

import ch.heia.isc.backend.model.ParticipantEntity;
import ch.heia.isc.backend.model.ParticipationEntity;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDate;
import java.util.List;

@org.springframework.stereotype.Repository
public interface ParticipationEntityRepository extends CrudRepository<ParticipationEntity, String> {
    List<ParticipationEntity> findAll();

    // Count the number of participants for a given activity
    int countByIdActivityAndDateActivity(String activity, LocalDate dateActivity);


    // Return if the user is participating to the activity
    boolean existsByIdActivityAndDateActivityAndParticipant(String activity, LocalDate dateActivity, String participant);

    // Delete the participation of the user to the activity
    void deleteParticipationEntityByIdActivityAndDateActivityAndParticipant(String activity, LocalDate dateActivity, String participant);

    List<ParticipationEntity> findAllByIdActivityAndDateActivity(String activity, LocalDate dateActivity);
    

}