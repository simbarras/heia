package net.collaud.gaetan.kafka.rainaggregator.stream;

import lombok.Data;
import net.collaud.gaetan.kafka.rainaggregator.koala.KoalaMessage;
import net.collaud.gaetan.kafka.rainaggregator.koala.KoalaValue;
import net.collaud.gaetan.kafka.rainaggregator.koala.type.MetricType;
import net.collaud.gaetan.kafka.rainaggregator.serialization.ObjectMapperSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class RainAggregatorTopology {

    public static final String INPUT_TOPIC = "device-values";
    public static final String OUTPUT_TOPIC = "device-BARRAS";
    static Logger log = LoggerFactory.getLogger(RainAggregatorTopology.class);

    public static ObjectMapperSerde<KoalaMessage> getKoalaMessageSerde() {
        return new ObjectMapperSerde<>(KoalaMessage.class);
    }

    public static void main(String[] args) {
        final RainAggregatorTopology rainAggregatorTopology = new RainAggregatorTopology();
        final Topology topology = rainAggregatorTopology.buildTopology();

        log.info("Topology\n\n{}\n", topology.describe());

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "BARRAS-3");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "concsys.collaud.me:29092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.setUncaughtExceptionHandler(
                throwable -> {
                    log.error("Error while processing data", throwable);
                    return null;
                });
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        streams.start();
    }

    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        final ObjectMapperSerde<KoalaMessage> koalaMessageSerde = getKoalaMessageSerde();

        SlidingWindows slidingWindows = SlidingWindows.ofTimeDifferenceWithNoGrace(Duration.ofHours(24));

        builder
                .stream(INPUT_TOPIC, Consumed.with(Serdes.String(), koalaMessageSerde).withName("source"))

                .groupBy((key, value) -> value.getDeviceId(), Grouped.with(Serdes.String(), koalaMessageSerde))
                .windowedBy(slidingWindows)
                .aggregate(
                        RainBy24H::new,
                        (key, value, aggregate) -> {
                            aggregate.computeRainBy24H(value);
                            return aggregate;
                        },
                        Materialized.with(Serdes.String(), new ObjectMapperSerde<>(RainBy24H.class))
                )
                .toStream()
                .map((key, value) -> {
                    if (value.lastMessage.getValues().stream().anyMatch(v -> v.getType() == MetricType.RAIN_MM)) {
                        KoalaValue koalaValue = new KoalaValue(MetricType.AGGREGATE_RAIN_MM_24H, value.rain);
                        koalaValue.setChannel(value.lastMessage.getValues().get(0).getChannel());
                        value.lastMessage.getValues().add(koalaValue);
                    }
                    return new KeyValue<>(value.lastMessage.getUniqueHash(), value.lastMessage);
                })
                .peek(
                        (key, value) -> log.info("Writing key={}, value={}", key, value),
                        Named.as("peek-key-and-value"))
                .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), koalaMessageSerde).withName("output"));

        return builder.build();
    }

    @Data
    private static class RainBy24H {
        private double rain;

        private KoalaMessage lastMessage;

        public RainBy24H() {
            this.rain = 0;
        }

        public void computeRainBy24H(KoalaMessage koalaMessage) {
            lastMessage = koalaMessage;
            // For each value, if it's a RAIN_MM, add it to the total
            final List<KoalaValue> values = koalaMessage.getValues();
            for (KoalaValue value : values) {
                if (value.getType() == MetricType.RAIN_MM) {
                    rain += value.getValue();
                }
            }
        }
    }
}
