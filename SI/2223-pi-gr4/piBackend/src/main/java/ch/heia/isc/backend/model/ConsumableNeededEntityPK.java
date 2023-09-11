package ch.heia.isc.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.util.Objects;

public class ConsumableNeededEntityPK implements Serializable {
    @Column(name = "activity")
    @Id
    private String activity;
    @Column(name = "consumable")
    @Id
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
        ConsumableNeededEntityPK that = (ConsumableNeededEntityPK) o;
        return Objects.equals(activity, that.activity) && Objects.equals(consumable, that.consumable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activity, consumable);
    }
}
