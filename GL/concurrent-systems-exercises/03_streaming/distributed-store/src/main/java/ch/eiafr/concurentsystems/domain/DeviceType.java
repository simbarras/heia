package ch.eiafr.concurentsystems.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public enum DeviceType {
    TEMPERATURE_HUMIDITY,
    WIND,
    RAIN,
}
