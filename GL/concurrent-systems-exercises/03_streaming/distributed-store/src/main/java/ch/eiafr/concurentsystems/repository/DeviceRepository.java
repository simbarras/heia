package ch.eiafr.concurentsystems.repository;

import ch.eiafr.concurentsystems.domain.DeviceEO;
import ch.eiafr.concurentsystems.exceptions.DoNotExistException;
import ch.eiafr.concurentsystems.exceptions.NameAlreadyExistException;
import io.quarkus.logging.Log;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DeviceRepository {

    @Inject OrganizationRepository organizationRepository;

    private static final Map<UUID, DeviceEO> DEVICES = new HashMap<>();

    public synchronized List<DeviceEO> getDevices(int pageSize, String after) {
        Log.infov("Finding {0} devices after {1}", pageSize, after);
        return DEVICES.values().stream()
                .sorted(Comparator.comparing(DeviceEO::getName))
                .filter(d -> after == null || d.getName().compareTo(after) > 0)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public synchronized DeviceEO findOne(UUID uuid) {
        Log.infov("Finding device with id: {0}", uuid);
        return Optional.ofNullable(DEVICES.get(uuid))
                .orElseThrow(
                        () ->
                                new DoNotExistException(
                                        "No device found with the uuid '" + uuid + "'"));
    }

    public synchronized DeviceEO save(DeviceEO device) {
        Log.infov("Saving device {0}", device);
        // TODO check nullable fields
        if (device.getUuid() == null) {
            device.setUuid(UUID.randomUUID());
            device.setCreatedAt(Instant.now());
        } else {
            device.setModifiedAt(Instant.now());
        }

        // check that the organization actually exists
        organizationRepository.findOne(device.getOrganizationUuid());

        if (DEVICES.values().stream()
                .anyMatch(
                        d ->
                                d.getName().equalsIgnoreCase(device.getName())
                                        && !d.getUuid().equals(device.getUuid()))) {
            throw new NameAlreadyExistException(
                    "The name '" + device.getName() + "' is already taken");
        }

        DEVICES.put(device.getUuid(), device);

        return device;
    }

    public synchronized DeviceEO delete(UUID uuid) {
        Log.infov("Deleting device with id: {0}", uuid);
        if (DEVICES.containsKey(uuid)) {
            return DEVICES.remove(uuid);
        } else {
            throw new IllegalArgumentException("No device with uuid '" + uuid + "' found");
        }
    }

    public List<DeviceEO> findByOrganizationId(UUID uuid) {
        return DEVICES.values().stream()
                .filter(device -> device.getOrganizationUuid().equals(uuid))
                .collect(Collectors.toList());
    }
}
