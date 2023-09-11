package ch.eiafr.concurentsystems.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class OrganizationEO implements Entity {
    private UUID uuid;
    private String name;
    private Instant createdAt;
}
