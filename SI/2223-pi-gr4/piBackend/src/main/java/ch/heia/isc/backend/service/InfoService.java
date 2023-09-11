package ch.heia.isc.backend.service;

import ch.heia.isc.backend.PiBackendApplication;
import ch.heia.isc.backend.model.InfoEntity;
import ch.heia.isc.backend.repository.InfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;

@Service
@ApplicationScope
public class InfoService {

    @Autowired
    private InfoRepository infoRepository;

    @Value("${sentry.release}")
    private String release;

    public List<InfoEntity> getAllInfos() {

        return infoRepository.findAll();
    }

    /**
     * Get the API version
     * @return InfoEntity
     */
    public InfoEntity getApiHealth() {
        InfoEntity info = new InfoEntity();
        info.setId("api_version");
        info.setValue(release);
        return info;
    }

    public InfoEntity getDbHealth() {
        return infoRepository.findById("db_version").orElse(new InfoEntity("db_version", "0.0.0"));
    }

    public InfoEntity updateDbHealth(String version) {
        InfoEntity info = getDbHealth();
        info.setValue(version);
        return infoRepository.save(info);
    }
}
