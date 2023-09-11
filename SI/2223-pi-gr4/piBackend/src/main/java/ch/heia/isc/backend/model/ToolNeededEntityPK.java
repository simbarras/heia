package ch.heia.isc.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.util.Objects;

public class ToolNeededEntityPK implements Serializable {
    @Column(name = "activity")
    @Id
    private String activity;
    @Column(name = "tool")
    @Id
    private String tool;

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getName() {
        return tool;
    }

    public void setName(String tool) {
        this.tool = tool;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToolNeededEntityPK that = (ToolNeededEntityPK) o;
        return Objects.equals(activity, that.activity) && Objects.equals(tool, that.tool);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activity, tool);
    }
}
