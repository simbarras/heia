package ch.heia.isc.backend.controller;

import ch.heia.isc.backend.model.ConsumableEntity;
import ch.heia.isc.backend.service.ConsumableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class ConsumableController {
    private final String BASE_URL = "/api/v1";
    private final ConsumableService consumableService;

    @Autowired
    public ConsumableController(ConsumableService consumableService) {
        this.consumableService = consumableService;
    }

    /**
     * Get all consumables
     * @return List of consumables
     */
    @GetMapping(path = BASE_URL + "/consumables", produces = "application/json")
    public List<ConsumableEntity> getConsumables() {
        return consumableService.getAllConsumables();
    }
}
