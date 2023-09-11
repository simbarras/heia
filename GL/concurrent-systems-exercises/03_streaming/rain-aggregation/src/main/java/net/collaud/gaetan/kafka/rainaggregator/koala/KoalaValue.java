package net.collaud.gaetan.kafka.rainaggregator.koala;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.collaud.gaetan.kafka.rainaggregator.koala.type.MetricType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KoalaValue {
    private MetricType type;
    private byte channel;
    private double value;

    // Construct with no channel
    public KoalaValue(MetricType type, double value) {
        this(type, (byte) -1, value);
    }
}
