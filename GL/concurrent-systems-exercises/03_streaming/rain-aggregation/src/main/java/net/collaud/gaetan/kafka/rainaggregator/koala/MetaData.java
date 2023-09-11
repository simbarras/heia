package net.collaud.gaetan.kafka.rainaggregator.koala;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetaData {
  private String payloadHex;
  private LoraNetworkInformation loraNetworkInformation;
}
