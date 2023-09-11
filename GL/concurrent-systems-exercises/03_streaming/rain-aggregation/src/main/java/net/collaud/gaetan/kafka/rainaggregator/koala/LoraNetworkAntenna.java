package net.collaud.gaetan.kafka.rainaggregator.koala;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoraNetworkAntenna {
  private String antennaId;
  private Double latitude;
  private Double longitude;
  private Double altitude;
  private Integer channel;
  private Double rssi;
  private Double snr;
  private Instant receptionTime;
}
