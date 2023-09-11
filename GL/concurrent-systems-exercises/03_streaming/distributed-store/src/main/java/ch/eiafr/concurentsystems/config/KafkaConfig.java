package ch.eiafr.concurentsystems.config;

import io.smallrye.config.WithDefault;
import java.util.Optional;

public interface KafkaConfig {

    /** Kafka bootstrap server */
    String bootstrapServers();

    /** Consumer group name */
    Optional<String> consumerGroupName();

    /** Max poll record (for consumer only) */
    @WithDefault("1000")
    int maxPollRecord();

    @WithDefault("earliest")
    String autoOffsetReset();
}
