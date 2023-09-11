package ch.eiafr.concurentsystems.kafka;

import ch.eiafr.concurentsystems.domain.DeviceEO;
import ch.eiafr.concurentsystems.domain.OrganizationEO;
import ch.eiafr.concurentsystems.kafka.entities.ChangeEO;
import ch.eiafr.concurentsystems.kafka.entities.ChangeType;
import ch.eiafr.concurentsystems.kafka.entities.EntityType;
import ch.eiafr.concurentsystems.repository.DeviceRepository;
import ch.eiafr.concurentsystems.repository.OrganizationRepository;
import io.quarkus.logging.Log;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.RecordDeserializationException;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class ChangeDataCaptureReader implements Runnable {

    private final OrganizationRepository organizationRepository;
    private final DeviceRepository deviceRepository;
    private final KafkaConsumer<String, ChangeEO> consumer;
    private final String topic;
    private final AtomicBoolean running = new AtomicBoolean(true);

    @Override
    public void run() {
        Log.infov("Subscribing to topic {0}", topic);
        consumer.subscribe(Collections.singleton(topic));
        while (running.get()) {
            try {
                final ConsumerRecords<String, ChangeEO> records = consumer.poll(Duration.ofSeconds(1));
                if (!records.isEmpty()) {
                    Log.infov("{0} records received", records.count());

                    records.forEach((ConsumerRecord<String, ChangeEO> record) -> {
                        ChangeEO change = record.value();
                        if (change != null) {
                            Log.infov("Change received: {0}", change);
                            if (change.getChangeType() == ChangeType.UPDATE) {
                                if (change.getEntityType() == EntityType.ORGANIZATION) {
                                    organizationRepository.save((OrganizationEO) change.getEntity());
                                } else if (change.getEntityType() == EntityType.DEVICE) {
                                    deviceRepository.save((DeviceEO) change.getEntity());
                                }
                            } else if (change.getChangeType() == ChangeType.DELETE) {
                                if (change.getEntityType() == EntityType.ORGANIZATION) {
                                    organizationRepository.delete(change.getEntity().getUuid());
                                } else if (change.getEntityType() == EntityType.DEVICE) {
                                    deviceRepository.delete(change.getEntity().getUuid());
                                }
                            }
                        }
                    });
                }
            } catch (RecordDeserializationException ex) {
                Log.errorv(ex, "Error while consuming record");
                // skip to the next offset
                consumer.seek(ex.topicPartition(), ex.offset() + 1);
            }
        }
        consumer.unsubscribe();
        consumer.close();
    }

    public void stop() {
        Log.infov("Unsubscribing and stopping consumer");
        running.set(false);
        consumer.wakeup();
    }
}
