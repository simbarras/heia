package net.collaud.gaetan.kafka.rainaggregator.stream;

import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import lombok.Data;
import net.collaud.gaetan.kafka.rainaggregator.koala.KoalaMessage;
import net.collaud.gaetan.kafka.rainaggregator.koala.KoalaValue;
import net.collaud.gaetan.kafka.rainaggregator.koala.type.MetricType;
import net.collaud.gaetan.kafka.rainaggregator.serialization.ObjectMapperSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class DownTimeFinder {

    public static final String INPUT_TOPIC = "device-values";
    static Logger log = LoggerFactory.getLogger(DownTimeFinder.class);

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "BARRAS-DOWNTIME-7");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "concsys.collaud.me:29092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(List.of(INPUT_TOPIC));

        ConsumerRecords<String, String> records;
        HashMap<String, Boolean> alreadyTraited = new HashMap<>();
        do {
            records = consumer.poll(Duration.ofSeconds(5));
            records.forEach(record -> {
                if (!alreadyTraited.containsKey(record.key())) {
                    alreadyTraited.put(record.key(), true);
                    String[] values = record.value().split(",");
                    String[] receptionTime = values[2].split(":");
                    String receptionTimeStr = receptionTime[1] + ":" + receptionTime[2] + ":" + receptionTime[3];
                    receptionTimeStr = receptionTimeStr.substring(1, receptionTimeStr.length() - 1);
                    Instant receptionTimeInstant = Instant.parse(receptionTimeStr);
                    Instant timestampInstant = Instant.ofEpochMilli(record.timestamp());
                    Duration delay = Duration.between(receptionTimeInstant, timestampInstant);
                    if (delay.toMinutes() > 30) {
                        log.info("Breakdown detected at: {}\tdelay of: {}", receptionTimeInstant, delay);
                    }
                }
            });
            log.info("Records analyzed by this step: {}", records.count());
        } while (records.count() > 0);
        consumer.unsubscribe();
        consumer.close();

    }
}
