package ch.heia.isc.backend.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "consumable_needed", schema = "public", catalog = "postgres")
@IdClass(ConsumableNeededEntityPK.class)
public class ConsumableNeededEntity {
    @Id
    @Column(name = "activity")
    private String activity;
    @Id
    @Column(name = "consumable")
    private String consumable;

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getConsumable() {
        return consumable;
    }

    public void setConsumable(String consumable) {
        this.consumable = consumable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsumableNeededEntity that = (ConsumableNeededEntity) o;
        return Objects.equals(activity, that.activity) && Objects.equals(consumable, that.consumable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activity, consumable);
    }
}
