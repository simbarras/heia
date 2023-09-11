package ch.eiafr.concurentsystems.api.rest.mapper;

import ch.eiafr.concurentsystems.api.rest.data.OrganizationTO;
import ch.eiafr.concurentsystems.domain.OrganizationEO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrganizationMapper {

    OrganizationMapper INSTANCE = Mappers.getMapper(OrganizationMapper.class);

    OrganizationTO map(OrganizationEO eo);

    OrganizationEO map(OrganizationTO eo);

    List<OrganizationTO> map(List<OrganizationEO> eo);
}
