package ch.eiafr.concurentsystems.kafka.entities;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class ChangeEODeserializer extends ObjectMapperDeserializer<ChangeEO> {
    public ChangeEODeserializer() {
        super(ChangeEO.class);
    }
}
