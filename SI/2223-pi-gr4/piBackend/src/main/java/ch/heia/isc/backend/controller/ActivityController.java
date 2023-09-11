package ch.heia.isc.backend.controller;

import ch.heia.isc.backend.model.ActivityEntity;
import ch.heia.isc.backend.model.ActivityFull;
import ch.heia.isc.backend.service.ActivityService;
import ch.heia.isc.backend.service.ConcurrentModificationException;
import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@RestController
public class ActivityController {
    private final String BASE_URL = "/api/v1";
    private final ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }


    /**
     * Get all activities
     *
     * @return List of activities
     */
    @GetMapping(path = BASE_URL + "/activities", produces = "application/json")
    public List<ActivityFull> activities() {
        return activityService.getAllActivitiesFull();
    }

    /**
     * Get activity by id
     *
     * @param id activity id
     * @return activity
     */
    @GetMapping(path = BASE_URL + "/activity/{id}", produces = "application/json")
    public String activity(@PathVariable("id") String id) {
        return "activity " + id;
    }

    /**
     * Add activity
     *
     * @param body activity
     * @return activity
     * @throws ParseException
     */
    @PostMapping(path = BASE_URL + "/activity", produces = "application/json")
    public ActivityEntity addActivity(@RequestBody String body) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonBody = (JSONObject) parser.parse(body);
        System.out.println(jsonBody);
        ActivityEntity activity = new ActivityEntity();
        activity.setName((String) jsonBody.get("name"));
        int maxPerson = Integer.parseInt(Objects.requireNonNull(jsonBody.get("maxPerson")).toString());
        activity.setMaxPerson(maxPerson);
        int minPerson = Integer.parseInt(Objects.requireNonNull(jsonBody.get("minPerson")).toString());
        activity.setMinPerson(minPerson);
        activity.setResponsables((String) jsonBody.get("responsables"));
        String img = (String) jsonBody.get("image");
        byte[] image = img.getBytes();
        activity.setImage(image);
        JSONArray dates = (JSONArray) jsonBody.get("dateList");
        activity.setDateList(dates.toString());
        double price = Double.parseDouble(Objects.requireNonNull(jsonBody.get("price")).toString());
        activity.setPrice(price);
        activity.setCategory((String) jsonBody.get("category"));
        JSONObject localization = (JSONObject) jsonBody.get("localization");
        String str = localization.toString();
        activity.setLocalization(str);

        // Add tools and consumables
        JSONArray toolsArray = (JSONArray) jsonBody.get("tools");
        JSONArray consumablesArray = (JSONArray) jsonBody.get("consumables");

        List<String> tools = new ArrayList<>();
        List<String> consumables = new ArrayList<>();
        for (Object o : toolsArray) {
            tools.add((String) o);
        }
        for (Object o : consumablesArray) {
            consumables.add((String) o);
        }
        activityService.addActivity(activity);

        activityService.setConsumablesInActivity(activity.getId(), consumables);
        activityService.setToolsInActivity(activity.getId(), tools);
        return activity;
    }

    /**
     * Publish the activity (set the activity as published)
     *
     * @param body activity information
     * @return activity
     */
    @PostMapping(path = BASE_URL + "/publish", produces = "application/json")
    public ActivityEntity publishActivity(@RequestBody String body) throws ParseException {
        System.out.println(body);
        // Parse the id
        JSONParser parser = new JSONParser();
        JSONObject jsonBody = (JSONObject) parser.parse(body);
        String id = (String) jsonBody.get("id");
        return activityService.publishActivity(id);
    }

    /**
     * Update the activity
     *
     * @param body activity information
     * @return activity
     * @throws ParseException
     * return activity
     */
    @PutMapping(path = BASE_URL + "/activity", produces = "application/json")
    public ActivityFull modifyActivity(@RequestBody String body, HttpServletResponse response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonBody = (JSONObject) parser.parse(body);
        System.out.println(jsonBody);

        ActivityFull activity = null;
        try {
            activity = activityService.modifyActivity(jsonBody);
        } catch (ConcurrentModificationException e) {
            // Send error
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return e.getActivityFull();
        }
        return activity;

    }
}
