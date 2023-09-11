package ch.eiafr.concurentsystems.kafka.entities;

import ch.eiafr.concurentsystems.domain.DeviceEO;
import ch.eiafr.concurentsystems.domain.Entity;
import ch.eiafr.concurentsystems.domain.OrganizationEO;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@RegisterForReflection
public class ChangeEO {
    private Instant changeTime;
    private EntityType entityType;
    private ChangeType changeType;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            property = "entityType",
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
    @JsonSubTypes(
            value = {
                @JsonSubTypes.Type(value = DeviceEO.class, name = "DEVICE"),
                @JsonSubTypes.Type(value = OrganizationEO.class, name = "ORGANIZATION")
            })
    private Entity entity;
}
