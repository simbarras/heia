package ch.eiafr.concurentsystems.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "app")
public interface AppConfig {

    KafkaConfig kafka();

    /**
     * Topic for CDC
     *
     * @return
     */
    String cdcTopic();
    String commandTopic();
}
