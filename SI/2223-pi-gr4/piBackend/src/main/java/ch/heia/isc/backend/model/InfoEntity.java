package ch.heia.isc.backend.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "info", schema = "public", catalog = "postgres")
public class InfoEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private String id;

    public InfoEntity(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public InfoEntity() {
    }

    @Basic
    @Column(name = "value")
    private String value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfoEntity that = (InfoEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value);
    }
}
