package ch.eiafr.concurentsystems.service;

import ch.eiafr.concurentsystems.domain.DeviceEO;
import ch.eiafr.concurentsystems.kafka.ChangeDataCaptureService;
import ch.eiafr.concurentsystems.repository.DeviceRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final ChangeDataCaptureService changeDataCaptureService;

    private Map<UUID, DeviceEO> devices = new HashMap<>();

    public synchronized List<DeviceEO> getDevices(int pageSize, String after) {
        return deviceRepository.getDevices(pageSize, after);
    }

    public synchronized DeviceEO findOne(UUID uuid) {
        return deviceRepository.findOne(uuid);
    }

//    public synchronized DeviceEO save(DeviceEO device) {
//        final DeviceEO saved = deviceRepository.save(device);
//        changeDataCaptureService.saveDevice(saved);
//        return saved;
//    }
//
//    public synchronized DeviceEO delete(UUID uuid) {
//        final DeviceEO removed = deviceRepository.delete(uuid);
//        changeDataCaptureService.deleteDevice(removed);
//        return removed;
//    }

}
