package ch.heia.isc.backend.model;

import java.util.ArrayList;
import java.util.List;

public class ActivityFull {

    private String id;

    private String name;

    private int maxPerson;

    private int minPerson;

    private String responsables;

    private byte[] image;

    private double price;

    private String localization;

    private String dateList;

    private String category;


    private List<String> consumables;
    private List<String> tools;
    private ActivityEntity activity;

    private long modificationId;

    private List<OccursEntity> occursEntities;

    public ActivityFull(ActivityEntity activity) {
        this.activity = activity;
        this.id = activity.getId();
        this.name = activity.getName();
        this.maxPerson = activity.getMaxPerson();
        this.minPerson = activity.getMinPerson();
        this.responsables = activity.getResponsables();
        this.image = activity.getImage();
        this.price = activity.getPrice();
        this.localization = activity.getLocalization();
        this.modificationId = activity.getModificationId();
        this.dateList = activity.getDateList();
        this.category = activity.getCategory();
        this.tools = new ArrayList<>();
        this.consumables = new ArrayList<>();
    }


    public List<String> getConsumables() {
        return consumables;
    }

    public void setConsumables(List<String> consumables) {
        this.consumables = consumables;
    }

    public List<String> getTools() {
        return tools;
    }

    public void setTools(List<String> tools) {
        this.tools = tools;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMaxPerson(int maxPerson) {
        this.maxPerson = maxPerson;
    }

    public void setMinPerson(int minPerson) {
        this.minPerson = minPerson;
    }

    public void setResponsables(String responsables) {
        this.responsables = responsables;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setLocalization(String localization) {
        this.localization = localization;
    }

    public void setDateList(String dateList) {
        this.dateList = dateList;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setActivity(ActivityEntity activity) {
        this.activity = activity;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaxPerson() {
        return maxPerson;
    }

    public int getMinPerson() {
        return minPerson;
    }

    public String getResponsables() {
        return responsables;
    }

    public byte[] getImage() {
        return image;
    }

    public double getPrice() {
        return price;
    }

    public String getLocalization() {
        return localization;
    }

    public String getDateList() {
        return dateList;
    }

    public String getCategory() {
        return category;
    }

    public ActivityEntity getActivity() {
        ActivityEntity result = new ActivityEntity();
        result.setId(this.id);
        result.setName(this.name);
        result.setMaxPerson(this.maxPerson);
        result.setMinPerson(this.minPerson);
        result.setResponsables(this.responsables);
        result.setImage(this.image);
        result.setPrice(this.price);
        result.setLocalization(this.localization);
        result.setDateList(this.dateList);
        result.setCategory(this.category);
        result.setModificationId(this.modificationId);
        return result;
    }

    public List<OccursEntity> getOccursEntities() {
        return occursEntities;
    }

    public void setOccursEntities(List<OccursEntity> occursEntities) {
        this.occursEntities = occursEntities;
    }

    public long getModificationId() {
        return modificationId;
    }

    public void setModificationId(long modificationId) {
        this.modificationId = modificationId;
    }
}
