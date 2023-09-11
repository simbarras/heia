package ch.heia.isc.backend.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "tool_needed", schema = "public", catalog = "postgres")
@IdClass(ToolNeededEntityPK.class)
public class ToolNeededEntity {
    @Id
    @Column(name = "activity")
    private String activity;

    @Id
    @Column(name = "tool")
    private String tool;

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToolNeededEntity that = (ToolNeededEntity) o;
        return Objects.equals(activity, that.activity) && Objects.equals(tool, that.tool);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activity, tool);
    }
}
