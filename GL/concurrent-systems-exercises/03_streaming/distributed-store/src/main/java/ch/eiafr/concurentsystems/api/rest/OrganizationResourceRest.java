package ch.eiafr.concurentsystems.api.rest;

import ch.eiafr.concurentsystems.api.rest.data.OrganizationTO;
import ch.eiafr.concurentsystems.api.rest.mapper.OrganizationMapper;
import ch.eiafr.concurentsystems.domain.OrganizationEO;
import ch.eiafr.concurentsystems.service.OrganizationService;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/api/v1/organizations")
public class OrganizationResourceRest {

    @Inject OrganizationService organizationService;

    private OrganizationMapper mapper = OrganizationMapper.INSTANCE;

    @GET
    public List<OrganizationTO> getAll() {
        return mapper.map(organizationService.findAll());
    }

    @GET
    @Path("{uuid}")
    public OrganizationTO getOne(@PathParam("uuid") UUID uuid) {
        return mapper.map(organizationService.findOne(uuid));
    }

//    @POST
//    public OrganizationTO insert(OrganizationTO org) {
//        return mapper.map(organizationService.save(mapper.map(org)));
//    }
//
//    @DELETE
//    @Path("{uuid}")
//    public OrganizationTO delete(@PathParam("uuid") UUID uuid) {
//        final OrganizationEO org = organizationService.findOne(uuid);
//        organizationService.delete(uuid);
//        return mapper.map(org);
//    }
}
