package ch.heia.isc.backend.model;

import jakarta.persistence.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "occurs", schema = "public", catalog = "postgres")
@IdClass(OccursEntityPK.class)
public class OccursEntity {
    @Id
    @Column(name = "id_activity")
    private String idActivity;

    @Id
    @Column(name = "date_activity")
    private LocalDate dateActivity;
    @Basic
    @Column(name = "canceled")
    private Boolean canceled;

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

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OccursEntity that = (OccursEntity) o;
        return Objects.equals(idActivity, that.idActivity) && Objects.equals(dateActivity, that.dateActivity) && Objects.equals(canceled, that.canceled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idActivity, dateActivity, canceled);
    }
}
