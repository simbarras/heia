package ch.heia.isc.backend.controller;

import ch.heia.isc.backend.model.InfoEntity;
import ch.heia.isc.backend.service.InfoService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class InfoController {
    private final String BASE_URL = "/api/v1";
    private final InfoService infoService;

    @Autowired
    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    /**
     * Get the health of the API
     * @return the information about the health of the API
     */
    @GetMapping(path = BASE_URL + "/health", produces = "application/json")
    public InfoEntity health() {
        return infoService.getApiHealth();
    }

    /**
     * Get the health of the database
     * @return the information about the health of the database
     */
    @GetMapping(path = BASE_URL + "/health/db", produces = "application/json")
    public InfoEntity healthDb() {
        return infoService.getDbHealth();
    }


    /**
     * Update the health of the database
     * @param body the version of the database
     * @return the information about the health of the database
     */
    @PostMapping(path = BASE_URL + "/health/db")
    @ResponseBody
    public InfoEntity healthDUpdate(@RequestBody(required = false) JSONObject body) {
        String version;
        try {

            version = body.get("version").toString();
        } catch (Exception e) {
            return infoService.getDbHealth();
        }
        return infoService.updateDbHealth(version);
    }
}
