package ch.eiafr.concurentsystems.kafka;

import ch.eiafr.concurentsystems.config.KafkaConfig;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;
import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

@ApplicationScoped
public class KafkaFactory {

    public <T> KafkaConsumer<String, T> createConsumer(
            KafkaConfig config, Class<? extends ObjectMapperDeserializer<T>> clazz) {
        return new KafkaConsumer<>(getConsumerProperties(config, clazz));
    }

    public <T> KafkaProducer<String, T> createProducer(KafkaConfig config, Class<T> clazz) {
        return new KafkaProducer<>(getProducerProperties(config, clazz));
    }

    private Properties getProducerProperties(KafkaConfig config, Class clazz) {
        final Properties props = getCommonProperties(config);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                ObjectMapperSerializer.class.getName());
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        return props;
    }

    private Properties getConsumerProperties(
            KafkaConfig config, Class<? extends ObjectMapperDeserializer<?>> clazz) {
        final Properties props = getCommonProperties(config);

        if (config.consumerGroupName() != null) {
            props.put(
                    ConsumerConfig.GROUP_ID_CONFIG,
                    config.consumerGroupName()
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "Consumer group name should be defined")));
        }
        if (config.autoOffsetReset() != null) {
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, config.autoOffsetReset());
        }

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, clazz.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, config.maxPollRecord());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        return props;
    }

    private Properties getCommonProperties(KafkaConfig config) {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServers());
        return props;
    }
}
