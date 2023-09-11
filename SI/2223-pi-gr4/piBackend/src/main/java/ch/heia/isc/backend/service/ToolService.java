package ch.heia.isc.backend.service;

import ch.heia.isc.backend.model.ToolEntity;
import ch.heia.isc.backend.repository.ToolEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

@Service
@ApplicationScope
public class ToolService {

    @Autowired
    private ToolEntityRepository toolEntityRepository;

    public List<ToolEntity> getAllTools() {
        return toolEntityRepository.findAll();
    }

}
