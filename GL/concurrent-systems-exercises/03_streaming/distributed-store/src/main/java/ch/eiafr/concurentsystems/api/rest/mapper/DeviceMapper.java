package ch.eiafr.concurentsystems.api.rest.mapper;

import ch.eiafr.concurentsystems.api.rest.data.DeviceTO;
import ch.eiafr.concurentsystems.domain.DeviceEO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DeviceMapper {

    DeviceMapper INSTANCE = Mappers.getMapper(DeviceMapper.class);

    DeviceTO map(DeviceEO eo);

    DeviceEO map(DeviceTO eo);

    List<DeviceTO> map(List<DeviceEO> eo);
}
