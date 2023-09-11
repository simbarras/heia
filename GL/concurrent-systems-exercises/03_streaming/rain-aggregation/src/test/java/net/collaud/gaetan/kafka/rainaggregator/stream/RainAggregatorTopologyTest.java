package net.collaud.gaetan.kafka.rainaggregator.stream;

import net.collaud.gaetan.kafka.rainaggregator.koala.KoalaMessage;
import net.collaud.gaetan.kafka.rainaggregator.koala.KoalaValue;
import net.collaud.gaetan.kafka.rainaggregator.koala.type.MetricType;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class RainAggregatorTopologyTest {

  private TopologyTestDriver testDriver;

  private TestInputTopic<String, KoalaMessage> inputTopic;
  private TestOutputTopic<String, KoalaMessage> outputTopic;

  @BeforeEach
  void setup() {
    Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);

    final Topology topology = new RainAggregatorTopology().buildTopology();
    testDriver = new TopologyTestDriver(topology, config);

    inputTopic =
            testDriver.createInputTopic(
                    RainAggregatorTopology.INPUT_TOPIC,
                    new StringSerializer(),
                    RainAggregatorTopology.getKoalaMessageSerde().serializer());

    outputTopic =
            testDriver.createOutputTopic(
                    RainAggregatorTopology.OUTPUT_TOPIC,
                    new StringDeserializer(),
                    RainAggregatorTopology.getKoalaMessageSerde().deserializer());
  }

  @AfterEach
  void tearDown() {
    if (testDriver != null) {
      testDriver.close();
    }
  }

  @Test
  void testNoRain() {
    inputTopic.pipeInput(generateKoalaMessageWithTemperature(1.5));
    assertThat(outputTopic.getQueueSize()).isEqualTo(1);
    assertThat(outputTopic.readValue().getValues())
            .extracting(KoalaValue::getType)
            .containsExactly(MetricType.AIR_TEMPERATURE_C);
  }

  @Test
  void testDirectValue() {
    inputTopic.pipeInput(generateKoalaMessageWithRain(1.5));
    assertThat(outputTopic.getQueueSize()).isEqualTo(1);
    assertThat(outputTopic.readValue().getValues())
            .extracting(KoalaValue::getType)
            .containsExactlyInAnyOrder(MetricType.RAIN_MM, MetricType.AGGREGATE_RAIN_MM_24H);
  }

  @Test
  void testAggregateTwoValues() {
    final KoalaMessage msg1 = generateKoalaMessageWithRain(1.5);
    final KoalaMessage msg2 = generateKoalaMessageWithRain(3.5);

    inputTopic.pipeInput(msg1);
    inputTopic.pipeInput(msg2);

    final Map<String, KoalaMessage> results = getValuesAsMap();
    assertThat(results).hasSize(2);

    final KoalaMessage record1 = results.get(msg1.getUniqueHash());
    final KoalaMessage record2 = results.get(msg2.getUniqueHash());

    assertThat(record1.getValues())
            .extracting(KoalaValue::getType)
            .containsExactlyInAnyOrder(MetricType.RAIN_MM, MetricType.AGGREGATE_RAIN_MM_24H);
    assertThat(record1.getValues())
            .extracting(KoalaValue::getValue)
            .containsExactlyInAnyOrder(1.5, 1.5);

    assertThat(record2.getValues())
            .extracting(KoalaValue::getType)
            .containsExactlyInAnyOrder(MetricType.RAIN_MM, MetricType.AGGREGATE_RAIN_MM_24H);
    assertThat(record2.getValues())
            .extracting(KoalaValue::getValue)
            .containsExactlyInAnyOrder(3.5, 5.0);
  }

  private Map<String, KoalaMessage> getValuesAsMap() {
    return outputTopic.readRecordsToList().stream()
            .collect(Collectors.toMap(r -> r.getKey(), r -> r.getValue(), (l, r) -> r));
  }

  @Test
  void testAggregateMultipleKeyValues() {
    final KoalaMessage msg1 = generateKoalaMessageWithRain("device-1", 1.5);
    final KoalaMessage msg2 = generateKoalaMessageWithRain("device-2", 1.3);
    final KoalaMessage msg3 = generateKoalaMessageWithRain("device-1", 3.5);
    final KoalaMessage msg4 = generateKoalaMessageWithRain("device-2", 3.3);

    inputTopic.pipeInput(msg1);
    inputTopic.pipeInput(msg2);
    inputTopic.pipeInput(msg3);
    inputTopic.pipeInput(msg4);

    final Map<String, KoalaMessage> results = getValuesAsMap();
    assertThat(results).hasSize(4);

    results
            .entrySet()
            .forEach(
                    rec -> {
                      assertThat(rec.getValue().getValues())
                              .extracting(KoalaValue::getType)
                              .containsExactlyInAnyOrder(MetricType.RAIN_MM, MetricType.AGGREGATE_RAIN_MM_24H);
                    });

    assertThat(results.get(msg1.getUniqueHash()).getDeviceId()).isEqualTo("device-1");
    assertThat(results.get(msg1.getUniqueHash()).getValues())
            .extracting(KoalaValue::getValue)
            .containsExactlyInAnyOrder(1.5, 1.5);

    assertThat(results.get(msg2.getUniqueHash()).getDeviceId()).isEqualTo("device-2");
    assertThat(results.get(msg2.getUniqueHash()).getValues())
            .extracting(KoalaValue::getValue)
            .containsExactlyInAnyOrder(1.3, 1.3);

    assertThat(results.get(msg3.getUniqueHash()).getDeviceId()).isEqualTo("device-1");
    assertThat(results.get(msg3.getUniqueHash()).getValues())
            .extracting(KoalaValue::getValue)
            .containsExactlyInAnyOrder(3.5, 5.0);

    assertThat(results.get(msg4.getUniqueHash()).getDeviceId()).isEqualTo("device-2");
    assertThat(results.get(msg4.getUniqueHash()).getValues())
            .extracting(KoalaValue::getValue)
            .containsExactlyInAnyOrder(3.3, 4.6);
  }

  @Test
  void testAggregatePeriod() {
    final Instant ref = Instant.now().truncatedTo(ChronoUnit.DAYS);
    final KoalaMessage msg1 = generateKoalaMessageWithRain(1.0);
    final KoalaMessage msg2 = generateKoalaMessageWithRain(2.0);
    final KoalaMessage msg3 = generateKoalaMessageWithRain(3.0);
    final KoalaMessage msg4 = generateKoalaMessageWithRain(4.0);

    inputTopic.pipeInput(msg1, ref.minus(Duration.ofHours(48)));
    inputTopic.pipeInput(msg2, ref.minus(Duration.ofHours(30)));
    inputTopic.pipeInput(msg3, ref.minus(Duration.ofHours(23)));
    inputTopic.pipeInput(msg4, ref.minus(Duration.ofHours(10)));

    final Map<String, KoalaMessage> results = getValuesAsMap();
    assertThat(results).hasSize(4);

    assertThat(results.get(msg1.getUniqueHash()).getValues())
            .extracting(KoalaValue::getValue)
            .containsExactlyInAnyOrder(1.0, 1.0);

    assertThat(results.get(msg2.getUniqueHash()).getValues())
            .extracting(KoalaValue::getValue)
            .containsExactlyInAnyOrder(2.0, 3.0);

    assertThat(results.get(msg3.getUniqueHash()).getValues())
            .extracting(KoalaValue::getValue)
            .containsExactlyInAnyOrder(3.0, 5.0);

    assertThat(results.get(msg4.getUniqueHash()).getValues())
            .extracting(KoalaValue::getValue)
            .containsExactlyInAnyOrder(4.0, 9.0);
  }

  KoalaMessage generateKoalaMessageWithRain(double rain) {
    return generateKoalaMessageWithRain("device-1", rain);
  }

  KoalaMessage generateKoalaMessageWithRain(String deviceId, double rain) {
    return KoalaMessage.builder()
            .uniqueHash(simulateHash())
            .eventUuid(UUID.randomUUID())
            .deviceId(deviceId)
            .receptionTime(Instant.now())
            .values(
                    Arrays.asList(
                            KoalaValue.builder()
                                    .type(MetricType.RAIN_MM)
                                    .channel((byte) 0)
                                    .value(rain)
                                    .build()))
            .build();
  }

  KoalaMessage generateKoalaMessageWithTemperature(double temp) {
    return KoalaMessage.builder()
            .uniqueHash(simulateHash())
            .eventUuid(UUID.randomUUID())
            .deviceId("device-1")
            .receptionTime(Instant.now())
            .values(
                    Arrays.asList(
                            KoalaValue.builder()
                                    .type(MetricType.AIR_TEMPERATURE_C)
                                    .channel((byte) 0)
                                    .value(temp)
                                    .build()))
            .build();
  }

  /**
   * Normally the hash is the md5 of the initial payload. Fot the sake of this unit test, it's not
   * needed, a simple random hash will do the trick.
   *
   * @return
   */
  private String simulateHash() {
    return String.valueOf(Math.abs(UUID.randomUUID().hashCode()));
  }
}
