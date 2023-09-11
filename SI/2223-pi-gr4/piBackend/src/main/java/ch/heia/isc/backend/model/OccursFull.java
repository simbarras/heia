package ch.heia.isc.backend.model;
import java.time.LocalDate;
import java.util.List;

public class OccursFull {

    private String id;

    private LocalDate dateActivity;

    private Boolean canceled;

    private String name;

    private Integer maxPerson;

    private Integer minPerson;

    private String responsables;

    private byte[] image;

    private double price;

    private Object localization;

    private String category;

    private List<String> tools;

    private List<String> consumables;

    private int nbPerson;

    private Boolean participate;

    public OccursFull(OccursViewEntity o) {
        this.id = o.getId();
        this.dateActivity = o.getDateActivity();
        this.canceled = o.getCanceled();
        this.name = o.getName();
        this.maxPerson = o.getMaxPerson();
        this.minPerson = o.getMinPerson();
        this.responsables = o.getResponsables();
        this.image = o.getImage();
        this.price = o.getPrice();
        this.localization = o.getLocalization();
        this.category = o.getCategory();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDateActivity() {
        return dateActivity;
    }

    public void setDateActivity(LocalDate dateActivity) {
        this.dateActivity = dateActivity;
    }

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxPerson() {
        return maxPerson;
    }

    public void setMaxPerson(Integer maxPerson) {
        this.maxPerson = maxPerson;
    }

    public Integer getMinPerson() {
        return minPerson;
    }

    public void setMinPerson(Integer minPerson) {
        this.minPerson = minPerson;
    }

    public String getResponsables() {
        return responsables;
    }

    public void setResponsables(String responsables) {
        this.responsables = responsables;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Object getLocalization() {
        return localization;
    }

    public void setLocalization(Object localization) {
        this.localization = localization;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getTools() {
        return tools;
    }

    public void setTools(List<String> tools) {
        this.tools = tools;
    }

    public List<String> getConsumables() {
        return consumables;
    }

    public void setConsumables(List<String> consumables) {
        this.consumables = consumables;
    }

    public int getNbPerson() {
        return nbPerson;
    }

    public void setNbPerson(int nbPerson) {
        this.nbPerson = nbPerson;
    }

    public Boolean getParticipate() {
        return participate;
    }

    public void setParticipate(Boolean participate) {
        this.participate = participate;
    }
}
