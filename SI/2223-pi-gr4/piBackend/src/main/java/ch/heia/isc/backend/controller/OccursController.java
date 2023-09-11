package ch.heia.isc.backend.controller;

import ch.heia.isc.backend.model.OccursEntity;
import ch.heia.isc.backend.service.OccursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OccursController {
    private final String BASE_URL = "/api/v1";
    private final OccursService occursService;

    @Autowired
    public OccursController(OccursService occursService) {
        this.occursService = occursService;
    }

    /**
     * Get all occurs
     * @return List of occurs
     */
    @GetMapping(path = BASE_URL + "/occurs", produces = "application/json")
    public List<OccursEntity> activities() {
        return occursService.getAllOccurs();
    }
}
