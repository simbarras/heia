package ch.heia.isc.backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Arrays;

import java.util.Objects;

@Entity
@Table(name = "occurs_view", schema = "public", catalog = "isc")
public class OccursViewEntity {


    @Basic
    @Column(name = "id")
    private String id;
    @Id
    @Basic
    @Column(name = "date_activity")
    private LocalDate dateActivity;

    @Basic
    @Column(name = "canceled")
    private Boolean canceled;

    @Basic
    @Column(name = "name")
    private String name;

    @Column(name = "max_person")
    private Integer maxPerson;
    @Basic
    @Column(name = "min_person")
    private Integer minPerson;
    @Basic
    @Column(name = "responsables")
    private String responsables;
    @Basic
    @Column(name = "image")
    private byte[] image;
    @Basic
    @Column(name = "price")
    private double price;
    @Basic
    @Column(name = "localization")
    private Object localization;
    @Basic
    @Column(name = "category")
    private String category;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OccursViewEntity that = (OccursViewEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(dateActivity, that.dateActivity) && Objects.equals(canceled, that.canceled) && Objects.equals(name, that.name) && Objects.equals(maxPerson, that.maxPerson) && Objects.equals(minPerson, that.minPerson) && Objects.equals(responsables, that.responsables) && Arrays.equals(image, that.image) && Objects.equals(price, that.price) && Objects.equals(localization, that.localization) && Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, dateActivity, canceled, name, maxPerson, minPerson, responsables, price, localization, category);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }


}
