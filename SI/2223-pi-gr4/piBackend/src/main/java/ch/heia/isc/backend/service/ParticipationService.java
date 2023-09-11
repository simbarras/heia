package ch.heia.isc.backend.service;

import ch.heia.isc.backend.model.OccursFull;
import ch.heia.isc.backend.model.ParticipantEntity;
import ch.heia.isc.backend.model.ParticipationEntity;
import ch.heia.isc.backend.model.User;
import ch.heia.isc.backend.repository.ParticipantRepository;
import ch.heia.isc.backend.repository.ParticipationEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.ApplicationScope;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@ApplicationScope
public class ParticipationService {

    @Autowired
    private ParticipationEntityRepository participationEntityRepository;

    @Autowired
    private OccursViewService occursViewService;

    @Autowired
    private ParticipantRepository participantRepository;

    /**
     * Update the participation status of a user for a given activity and date
     * @param activityId The activity id
     * @param occursDate The date of the activity
     * @param user The user
     * @return The updated occursFull
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public OccursFull updateParticipation(String activityId, LocalDate occursDate, User user) {
        // Check if the user is already participating
        boolean participate = participationEntityRepository.existsByIdActivityAndDateActivityAndParticipant(activityId, occursDate, user.getEmail());
        // If yes, remove the participation
        if (participate) {
            participationEntityRepository.deleteParticipationEntityByIdActivityAndDateActivityAndParticipant(activityId, occursDate, user.getEmail());
        } else {
            // If no, add the participation
            // Check if is there place for the user
            int nb = participationEntityRepository.countByIdActivityAndDateActivity(activityId, occursDate);
            int maxNb = occursViewService.getOccursFull(activityId, occursDate, false).getMaxPerson();
            System.out.println("nb: " + nb + " maxNb: " + maxNb);
            if (nb >= maxNb) {
                // If no, return an error
               return null;
            }
            // If yes, add the participation
            participationEntityRepository.save(new ParticipationEntity(activityId, occursDate, user.getEmail()));
        }
        // Update the participation status
        // Return the updated occursFull
        return occursViewService.getOccursFull(activityId, occursDate, !participate);
    }

    /** Return the list of participants for a given activity
     *
     * @param activityId The activity id
     * @param dateActivity The date of the activity
     * @return The list of participants
     */
    public List<ParticipantEntity> getParticipants(String activityId, LocalDate dateActivity) {
        List<ParticipationEntity> participationEntities = participationEntityRepository.findAllByIdActivityAndDateActivity(activityId, dateActivity);

        List<ParticipantEntity> participantEntities = new ArrayList<>();
        for (ParticipationEntity participationEntity : participationEntities) {
            ParticipantEntity participant = participantRepository.findByEmail(participationEntity.getParticipant()).orElse(null);
            participantEntities.add(participant);
        }
        return participantEntities;
    }
}
