package ch.heia.isc.backend.controller;

import ch.heia.isc.backend.model.ToolEntity;
import ch.heia.isc.backend.service.ToolService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class ToolController {
    private final String BASE_URL = "/api/v1";
    private final ToolService toolService;

    @Autowired
    public ToolController(ToolService toolService) {
        this.toolService = toolService;
    }

    /**
     * Get all tools
     * @return List of tools
     */
    @GetMapping(path = BASE_URL + "/tools", produces = "application/json")
    public List<ToolEntity> activities() {
        return toolService.getAllTools();
    }
}
