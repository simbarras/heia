package ch.heia.isc.backend.service;

import ch.heia.isc.backend.model.*;
import ch.heia.isc.backend.repository.ActivityRepository;
import ch.heia.isc.backend.repository.ConsumableNeededEntityRepository;
import ch.heia.isc.backend.repository.OccursRepository;
import ch.heia.isc.backend.repository.ToolNeededEntityRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.ApplicationScope;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@ApplicationScope
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ToolNeededEntityRepository toolNeededEntityRepository;

    @Autowired
    private ConsumableNeededEntityRepository consumableNeededEntityRepository;

    @Autowired
    private OccursRepository occursRepository;

    public List<ActivityEntity> getAllActivities() {
        return activityRepository.findAll();
    }

    /**
     * Add an activity
     * @param activity to add
     * @return activity added
     */
    public ActivityEntity addActivity(ActivityEntity activity) {
        return activityRepository.save(activity);
    }


    /**
     * Get the list of tools needed for an activity
     * @param activity activity id
     * @return list of tools
     */
    public List<String> getTools(String activity) {
        return toolNeededEntityRepository.findAllByActivity(activity).stream().map(ToolNeededEntity::getTool).toList();
    }

    /**
     * Get the list of consumables needed for an activity
     * @param id activity id
     * @return list of consumables
     */
    public List<String> getConsumables(String id) {

        return consumableNeededEntityRepository.findAllByActivity(id).stream().map(ConsumableNeededEntity::getConsumable).toList();
    }

    /**
     * Get all activities with the list of the occurrences, tools and consumables
     * @return list of activities (full)
     */
    public List<ActivityFull> getAllActivitiesFull() {
        return activityRepository.findAll().stream().map(a -> {
            ActivityFull af = new ActivityFull(a);
            af.setTools(getTools(a.getId()));
            af.setConsumables(getConsumables(a.getId()));
            af.setOccursEntities(getOccurs(a.getId()));
            System.out.println(af.getModificationId());
            return af;
        }).toList();
    }

    /**
     * Get the occurrences of an activity
     * @param id activity id
     * @return list of occurrences
     */
    public List<OccursEntity> getOccurs(String id) {
        return occursRepository.findAllByIdActivity(id);
    }

    /**
     * Set the tools needed for an activity
     * @param activity activity id
     * @param tools list of tools needed
     */
    public void setToolsInActivity(String activity, List<String> tools) {
        tools.forEach(t -> {
            ToolNeededEntity toolNeededEntity = new ToolNeededEntity();
            toolNeededEntity.setActivity(activity);
            toolNeededEntity.setTool(t);
            toolNeededEntityRepository.save(toolNeededEntity);
        });
    }

    /**
     * Set the consumables needed for an activity
     * @param activity activity id
     * @param consumables list of consumables needed
     */
    public void setConsumablesInActivity(String activity, List<String> consumables) {
        consumables.forEach(c -> {
            ConsumableNeededEntity consumableNeededEntity = new ConsumableNeededEntity();
            consumableNeededEntity.setActivity(activity);
            consumableNeededEntity.setConsumable(c);
            consumableNeededEntityRepository.save(consumableNeededEntity);
        });
    }

    /**
     * Publish an activity. Put the DateList to null and add the occurrences in function of the dateList
     * @param id activity id
     * @return activity published
     */
    public ActivityEntity publishActivity(String id) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

        ActivityEntity activityEntity = activityRepository.findById(id).orElseThrow();
        // Add occurs with date
        String dateList = activityEntity.getDateList();
        //Convert the string to json
        JSONArray jsonArray = (JSONArray) org.json.simple.JSONValue.parse(dateList);
        jsonArray.forEach(d -> {
            LocalDate date = LocalDate.parse(d.toString(), formatter);
            OccursEntity occursEntity = new OccursEntity();
            occursEntity.setIdActivity(id);
            occursEntity.setDateActivity(date);
            occursEntity.setCanceled(false);
            occursRepository.save(occursEntity);
        });
        activityEntity.setDateList(null);

        return activityRepository.save(activityEntity);
    }

    /**
     * Modify an activity and check if the activity has been modified by another user
     * @param jsonBody json with the activity to modify
     * @return activity modified
     * @throws ConcurrentModificationException if the activity has been modified by another user
     */
    @Modifying
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ActivityFull modifyActivity(JSONObject jsonBody) throws ConcurrentModificationException {
        String id = (String) jsonBody.get("id");
        ActivityFull activity = new ActivityFull(activityRepository.findById(id).orElseThrow());
        if (activity.getModificationId() != Long.parseLong(jsonBody.get("modificationId").toString())) {
            activity.setTools(getTools(id));
            activity.setConsumables(getConsumables(id));
            throw new ConcurrentModificationException("The activity has been modified by another user", activity);
        }
        activity.setModificationId(activity.getModificationId() + 1);

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
        for (Object o : toolsArray) {
            activity.getTools().add((String) o);
        }
        for (Object o : consumablesArray) {
            activity.getConsumables().add((String) o);
        }

        // Delete all tools and consumables
        toolNeededEntityRepository.deleteAllByActivity(activity.getId());
        consumableNeededEntityRepository.deleteAllByActivity(activity.getId());

        // Add new tools and consumables
        setToolsInActivity(activity.getId(), activity.getTools());
        setConsumablesInActivity(activity.getId(), activity.getConsumables());

        // Save activity
        activityRepository.save(activity.getActivity());
        System.out.println(activity.getMaxPerson());

        return activity;
    }
}
