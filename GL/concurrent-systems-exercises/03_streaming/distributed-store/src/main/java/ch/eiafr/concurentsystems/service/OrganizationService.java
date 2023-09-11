package ch.eiafr.concurentsystems.service;

import ch.eiafr.concurentsystems.domain.OrganizationEO;
import ch.eiafr.concurentsystems.kafka.ChangeDataCaptureService;
import ch.eiafr.concurentsystems.repository.OrganizationRepository;
import java.util.List;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;

    public synchronized List<OrganizationEO> findAll() {
        return organizationRepository.findAll();
    }

    public synchronized OrganizationEO findOne(UUID uuid) {
        return organizationRepository.findOne(uuid);
    }

//    public synchronized OrganizationEO save(OrganizationEO org) {
//        final OrganizationEO saved = organizationRepository.save(org);
//        changeDataCaptureService.saveOrganization(saved);
//        return saved;
//    }
//
//    public synchronized OrganizationEO delete(UUID uuid) {
//        final OrganizationEO deleted = organizationRepository.delete(uuid);
//        changeDataCaptureService.deleteOrganization(deleted);
//        return deleted;
//    }
}
