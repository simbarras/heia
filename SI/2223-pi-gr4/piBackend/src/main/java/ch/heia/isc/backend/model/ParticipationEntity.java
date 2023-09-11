package ch.heia.isc.backend.model;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "participation", schema = "public", catalog = "postgres")
@IdClass(ParticipationEntityPK.class)
public class ParticipationEntity {

    @Id
    @Column(name = "participant")
    private String participant;

    @Id
    @Column(name = "id_activity")
    private String idActivity;

    @Id
    @Column(name = "date_activity")
    private LocalDate dateActivity;

    public ParticipationEntity(String activityId, LocalDate occursDate, String email) {
        this.idActivity = activityId;
        this.dateActivity = occursDate;
        this.participant = email;
    }

    public ParticipationEntity() {

    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

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
        ParticipationEntity that = (ParticipationEntity) o;
        return Objects.equals(participant, that.participant) && Objects.equals(idActivity, that.idActivity) && Objects.equals(dateActivity, that.dateActivity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participant, idActivity, dateActivity);
    }
}
