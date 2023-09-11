package ch.heia.isc.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

public class OccursEntityPK implements Serializable {
    @Column(name = "id_activity")
    @Id
    private String idActivity;
    @Column(name = "date_activity")
    @Id
    private LocalDate dateActivity;

    public String getIdActivity() {
        return idActivity;
    }

    public void setIdActivity(String idActivity) {
        this.idActivity = idActivity;
    }

    public LocalDate getDateActivity() {
        return dateActivity;
    }

    public void setDateActivity(LocalDate dateActivity) {
        this.dateActivity = dateActivity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OccursEntityPK that = (OccursEntityPK) o;
        return Objects.equals(idActivity, that.idActivity) && Objects.equals(dateActivity, that.dateActivity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idActivity, dateActivity);
    }
}
