package ch.heia.isc.backend.controller;

import ch.heia.isc.backend.PiBackendApplication;
import ch.heia.isc.backend.model.OccursFull;
import ch.heia.isc.backend.model.ParticipantEntity;
import ch.heia.isc.backend.model.participation.ParticipationRequest;
import ch.heia.isc.backend.model.User;
import ch.heia.isc.backend.service.AuthService;
import ch.heia.isc.backend.service.OccursViewService;
import ch.heia.isc.backend.service.ParticipationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@RestController()
public class ParticipationController {

    private final ParticipationService participationService;
    private final AuthService authService;
    private final OccursViewService occursViewService;

    @Autowired
    public ParticipationController(ParticipationService participationService, AuthService authService, OccursViewService occursViewService) {
        this.participationService = participationService;
        this.authService = authService;
        this.occursViewService = occursViewService;
    }

    /**
     * Update the participation of a user to an activity
     *
     * @param request  contains the activity id, the date of the activity and the participation status
     * @param token    is the jwt token
     * @param response is the http response
     * @return the updated occurs
     */
    @PostMapping(path = PiBackendApplication.BASE_PATH + "/participation", produces = "application/json")
    public OccursFull updateParticipation(@RequestBody ParticipationRequest request, @RequestHeader("Authorization") String token, HttpServletResponse response) {
        // Get the user from the jwt token
        User user = authService.getCurrentUser(token);
        //String to LocalDate
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        LocalDate date = LocalDate.parse(request.getDateOccurs(), formatter);
        OccursFull result = participationService.updateParticipation(request.getActivityId(), date, user);
        if (result == null) {
            System.out.println("Error: no place for the user");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return occursViewService.getOccursFull(request.getActivityId(), date, false);
        }
        return result;
    }


    /**
     * Get the list of participants for a given occurs (idActivity and dateActivity)
     *
     * @param idActivity   is the id of the activity
     * @param dateActivity is the date of the activity
     * @return the list of participants
     */
    @GetMapping(path = PiBackendApplication.BASE_PATH + "/participation/{id}/{date}", produces = "application/json")
    public List<ParticipantEntity> getParticipants(@PathVariable("id") String idActivity, @PathVariable("date") String dateActivity) {
        //String to LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        LocalDate date = LocalDate.parse(dateActivity, formatter);
        return participationService.getParticipants(idActivity, date);
    }

}
