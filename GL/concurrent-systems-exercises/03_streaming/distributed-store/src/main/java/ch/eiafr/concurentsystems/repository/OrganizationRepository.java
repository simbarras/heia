package ch.eiafr.concurentsystems.repository;

import ch.eiafr.concurentsystems.domain.OrganizationEO;
import ch.eiafr.concurentsystems.exceptions.DoNotExistException;
import ch.eiafr.concurentsystems.exceptions.NameAlreadyExistException;
import io.quarkus.logging.Log;
import java.time.Instant;
import java.util.ArrayList;
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
public class OrganizationRepository {
    private static final Map<UUID, OrganizationEO> ORGANIZATIONS = new HashMap<>();

    @Inject DeviceRepository deviceRepository;

    public synchronized List<OrganizationEO> findAll() {
        Log.infov("Finding all organization");
        return new ArrayList<>(
                ORGANIZATIONS.values().stream()
                        .sorted(Comparator.comparing(OrganizationEO::getName))
                        .collect(Collectors.toList()));
    }

    public synchronized OrganizationEO findOne(UUID uuid) {
        Log.infov("Finding organization with id: {0}", uuid);
        return Optional.ofNullable(ORGANIZATIONS.get(uuid))
                .orElseThrow(
                        () ->
                                new DoNotExistException(
                                        "No organization found with the uuid '" + uuid + "'"));
    }

    public synchronized OrganizationEO findByName(String name) {
        Log.infov("Finding organization with name: {0}", name);
        return ORGANIZATIONS.values().stream()
                .filter(o -> o.getName().equalsIgnoreCase(name))
                .findAny()
                .orElseThrow(
                        () ->
                                new DoNotExistException(
                                        "No organization found with the name '" + name + "'"));
    }

    public synchronized OrganizationEO save(OrganizationEO org) {
        Log.infov("Saving organization {0}", org);
        // TODO check nullable fields
        if (org.getUuid() == null) {
            org.setUuid(UUID.randomUUID());
            org.setCreatedAt(Instant.now());
        }
        if (ORGANIZATIONS.values().stream()
                .anyMatch(
                        o ->
                                o.getName().equalsIgnoreCase(org.getName())
                                        && !o.getUuid().equals(org.getUuid()))) {
            throw new NameAlreadyExistException(
                    "The name '" + org.getName() + "' is already taken");
        }

        ORGANIZATIONS.put(org.getUuid(), org);

        return org;
    }

    public synchronized OrganizationEO delete(UUID uuid) {
        Log.infov("Deleting organization with id: {0}", uuid);

        if (deviceRepository.findByOrganizationId(uuid).isEmpty()) {
            if (ORGANIZATIONS.containsKey(uuid)) {
                return ORGANIZATIONS.remove(uuid);
            } else {
                throw new IllegalArgumentException(
                        "No organization with uuid '" + uuid + "' found");
            }
        } else {
            throw new IllegalArgumentException(
                    "Organization with uuid '" + uuid + "' has still devices attached to it");
        }
    }
}
