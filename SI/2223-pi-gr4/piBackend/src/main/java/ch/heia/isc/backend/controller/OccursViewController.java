package ch.heia.isc.backend.controller;

import ch.heia.isc.backend.model.OccursFull;
import ch.heia.isc.backend.model.OccursViewEntity;
import ch.heia.isc.backend.model.User;
import ch.heia.isc.backend.service.AuthService;
import ch.heia.isc.backend.service.OccursViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class OccursViewController {
    private final String BASE_URL = "/api/v1";
    private final OccursViewService occursViewService;
    private final AuthService authService;

    @Autowired
    public OccursViewController(OccursViewService occursViewService, AuthService authService) {
        this.occursViewService = occursViewService;
        this.authService = authService;
    }

    /**
     * Get all occurs with full information
     * @param token is optional
     * @return List of occurs with full information
     */
    @GetMapping(path = BASE_URL + "/occursView", produces = "application/json")
    public List<OccursFull> activities(@RequestHeader(value = "Authorization", required = false) String token) {
        // Extract the user from the token
        User user = null;
        if (token != null) {
            System.out.print("Search for user with token: " + token);
            user = authService.getCurrentUser(token);
        }
        return occursViewService.getAllOccursFull(user);
    }

}
