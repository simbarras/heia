package ch.eiafr.concurentsystems.api.rest.data;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
public class OrganizationTO {
    private UUID uuid;
    private String name;
    private Instant createdAt;
}
