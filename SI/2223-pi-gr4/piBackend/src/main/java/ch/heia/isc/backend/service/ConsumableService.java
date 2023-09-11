package ch.heia.isc.backend.service;

import ch.heia.isc.backend.model.ConsumableEntity;
import ch.heia.isc.backend.repository.ConsumableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

@Service
@ApplicationScope
public class ConsumableService {

    @Autowired
    private ConsumableRepository consumableRepository;

    /**
     * Get all consumables
     * @return List of consumables
     */
    public List<ConsumableEntity> getAllConsumables() {
        return consumableRepository.findAll();
    }

}
