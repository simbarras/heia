package ch.heia.isc.backend.service;

import ch.heia.isc.backend.model.*;
import ch.heia.isc.backend.model.User;
import ch.heia.isc.backend.repository.ConsumableNeededEntityRepository;
import ch.heia.isc.backend.repository.OccursViewRepository;
import ch.heia.isc.backend.repository.ParticipationEntityRepository;
import ch.heia.isc.backend.repository.ToolNeededEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.ApplicationScope;

import java.time.LocalDate;

import java.util.List;

@Service
@ApplicationScope
public class OccursViewService {

    @Autowired
    private OccursViewRepository occursViewRepository;

    @Autowired
    private ToolNeededEntityRepository toolNeededEntityRepository;

    @Autowired
    private ConsumableNeededEntityRepository consumableNeededEntityRepository;

    @Autowired
    private ParticipationEntityRepository participationEntityRepository;

    @Transactional(isolation = org.springframework.transaction.annotation.Isolation.READ_COMMITTED)
    public List<OccursViewEntity> getAllOccursView() {

        return occursViewRepository.findAllByOrderByDateActivityAsc();
    }

    /**
     * Get all occurs with tools, consumables, nbPerson and participate
     * @param user is optional (null if not logged in)
     * @return List of occurs with full information
     */
    @Transactional(isolation = org.springframework.transaction.annotation.Isolation.READ_COMMITTED)
    public List<OccursFull> getAllOccursFull(User user) {

        return getAllOccursView().stream().map(o -> {
            OccursFull of = new OccursFull(o);
            of.setTools(getTools(o.getId()));
            of.setConsumables(getConsumables(o.getId()));
            of.setNbPerson(getNbPerson(o.getId(), o.getDateActivity()));
            of.setParticipate(getParticipate(o.getId(), o.getDateActivity(), user));
            return of;
        }).toList();
    }

    /**
     * Get all occurs with tools, consumables, nbPerson and participate
     * @param id of the activity
     * @param date of the activity
     * @param participate true if the user participate to the activity
     * @return OccursFull with full information
     */
    @Transactional(isolation = org.springframework.transaction.annotation.Isolation.READ_COMMITTED)
    public OccursFull getOccursFull(String id, LocalDate date, boolean participate) {
        OccursViewEntity o = occursViewRepository.findByIdAndDateActivity(id, date);
        OccursFull of = new OccursFull(o);
        of.setTools(getTools(o.getId()));
        of.setConsumables(getConsumables(o.getId()));
        of.setNbPerson(getNbPerson(o.getId(), o.getDateActivity()));
        of.setParticipate(participate);
        return of;
    }


    private List<String> getTools(String activity) {
        return toolNeededEntityRepository.findAllByActivity(activity).stream().map(ToolNeededEntity::getTool).toList();
    }

    private List<String> getConsumables(String id) {

        return consumableNeededEntityRepository.findAllByActivity(id).stream().map(ConsumableNeededEntity::getConsumable).toList();
    }

    /**
     * Get the number of person who participate to an activity
     * @param id of the activity
     * @param date of the activity
     * @return number of person
     */
    private int getNbPerson(String id, LocalDate date) {
        return participationEntityRepository.countByIdActivityAndDateActivity(id, date);
    }

    /**
     * Check if a user participate to an activity
     * @param id of the activity
     * @param date of the activity
     * @param user is optional (null if not logged in)
     * @return true if the user participate to the activity
     */
    private boolean getParticipate(String id, LocalDate date, User user) {
        if (user == null) {
            return false;
        }
        return participationEntityRepository.existsByIdActivityAndDateActivityAndParticipant(id, date, user.getUsername());
    }

}