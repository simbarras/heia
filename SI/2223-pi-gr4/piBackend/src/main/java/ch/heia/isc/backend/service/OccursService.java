package ch.heia.isc.backend.service;

import ch.heia.isc.backend.model.OccursEntity;
import ch.heia.isc.backend.repository.OccursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

@Service
@ApplicationScope
public class OccursService {

    @Autowired
    private OccursRepository occursRepository;

    @Transactional(isolation = org.springframework.transaction.annotation.Isolation.READ_COMMITTED)
    public List<OccursEntity> getAllOccurs() {

        return occursRepository.findAll();
    }

}
