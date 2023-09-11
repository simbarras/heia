package net.collaud.gaetan.kafka.rainaggregator.koala;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.collaud.gaetan.kafka.rainaggregator.koala.type.Source;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KoalaMessage {
    private UUID eventUuid;
    private String uniqueHash;
    private Instant receptionTime;
    private String deviceId;
    private MetaData metaData;
    private Source source;
    private List<KoalaValue> values;
}
