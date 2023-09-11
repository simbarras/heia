package net.collaud.gaetan.kafka.rainaggregator.koala;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.collaud.gaetan.kafka.rainaggregator.koala.type.MetricType;
import net.collaud.gaetan.kafka.rainaggregator.koala.type.TriggerType;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KoalaAlarmConfiguration {
  private UUID uuid;
  private String deviceId;
  private String name;
  private TriggerType triggerType;
  private Double value;
  private MetricType dataType;
}
