package ch.eiafr.concurentsystems.kafka;

import ch.eiafr.concurentsystems.config.AppConfig;
import ch.eiafr.concurentsystems.domain.DeviceEO;
import ch.eiafr.concurentsystems.domain.Entity;
import ch.eiafr.concurentsystems.domain.OrganizationEO;
import ch.eiafr.concurentsystems.kafka.entities.ChangeEO;
import ch.eiafr.concurentsystems.kafka.entities.ChangeEODeserializer;
import ch.eiafr.concurentsystems.kafka.entities.ChangeType;
import ch.eiafr.concurentsystems.kafka.entities.EntityType;
import ch.eiafr.concurentsystems.repository.DeviceRepository;
import ch.eiafr.concurentsystems.repository.OrganizationRepository;
import io.quarkus.logging.Log;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

@ApplicationScoped
@RequiredArgsConstructor
public class ChangeDataCaptureService {

    private final OrganizationRepository organizationRepository;
    private final DeviceRepository deviceRepository;
    private final KafkaFactory kafkaFactory;
    private final AppConfig appConfig;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private KafkaProducer<String, ChangeEO> producer;
    private KafkaConsumer<String, ChangeEO> consumer;
    private ChangeDataCaptureReader changeDataCaptureReader;

    public void startupEvent(@Observes StartupEvent event) {
        producer = kafkaFactory.createProducer(appConfig.kafka(), ChangeEO.class);
        consumer = kafkaFactory.createConsumer(appConfig.kafka(), ChangeEODeserializer.class);

        changeDataCaptureReader =
                new ChangeDataCaptureReader(
                        organizationRepository, deviceRepository, consumer, appConfig.cdcTopic());
        Log.info("Starting CDC reader");
        executorService.submit(changeDataCaptureReader);
    }

    public void shutdownEvent(@Observes ShutdownEvent shutdownEvent) {
        if (changeDataCaptureReader != null) {
            changeDataCaptureReader.stop();
            changeDataCaptureReader = null;
        }
        Log.infov("Closing kafka producer and consumer");
        producer.close();
        // consumer is closed in the thread
    }

}
