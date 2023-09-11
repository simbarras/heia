package ch.heia.isc.backend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "activity", schema = "public", catalog = "postgres")
public class ActivityEntity {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @Column(name = "id")
    private String id;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "max_person")
    private int maxPerson;
    @Basic
    @Column(name = "min_person")
    private int minPerson;
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
    @JdbcTypeCode(value = SqlTypes.JSON)
    @Column(name = "localization")
    private String localization;
    @Basic
    @JdbcTypeCode(value = SqlTypes.JSON)
    @Column(name = "date_list")
    private String dateList;
    @Basic
    @Column(name = "category")
    private String category;

    @Basic
    @Column(name = "modification_id")
    private long modificationId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxPerson() {
        return maxPerson;
    }

    public void setMaxPerson(int maxPerson) {
        this.maxPerson = maxPerson;
    }

    public int getMinPerson() {
        return minPerson;
    }

    public void setMinPerson(int minPerson) {
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

    public String getLocalization() {
        return localization;
    }

    public void setLocalization(String localization) {
        this.localization = localization;
    }

    public String getDateList() {
        return dateList;
    }

    public void setDateList(String dateList) {
        this.dateList = dateList;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getModificationId() {
        return modificationId;
    }

    public void setModificationId(long lastModified) {
        this.modificationId = lastModified;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityEntity that = (ActivityEntity) o;
        return maxPerson == that.maxPerson && minPerson == that.minPerson && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(responsables, that.responsables) && Objects.equals(image, that.image) && Objects.equals(price, that.price) && Objects.equals(localization, that.localization) && Objects.equals(dateList, that.dateList) && Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, maxPerson, minPerson, responsables, price, localization, dateList, category, image);

        return result;
    }


}
