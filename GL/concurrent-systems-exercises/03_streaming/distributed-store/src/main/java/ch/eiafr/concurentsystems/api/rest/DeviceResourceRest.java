package ch.eiafr.concurentsystems.api.rest;

import ch.eiafr.concurentsystems.api.rest.data.DeviceTO;
import ch.eiafr.concurentsystems.api.rest.mapper.DeviceMapper;
import ch.eiafr.concurentsystems.domain.DeviceEO;
import ch.eiafr.concurentsystems.service.DeviceService;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/api/v1/devices")
public class DeviceResourceRest {

    @Inject DeviceService deviceService;

    private DeviceMapper mapper = DeviceMapper.INSTANCE;

    @GET
    public List<DeviceTO> getDevices(
            @QueryParam("limit") Integer limit, @QueryParam("after") String after) {
        if (limit == null) {
            limit = 10;
        }
        return mapper.map(deviceService.getDevices(limit, after));
    }

    @GET
    @Path("{uuid}")
    public DeviceTO getOneDevice(@PathParam("uuid") UUID uuid) {
        return mapper.map(deviceService.findOne(uuid));
    }

//    @POST
//    public DeviceTO insert(DeviceTO deviceTO) {
//        return mapper.map(deviceService.save(mapper.map(deviceTO)));
//    }
//
//    @PUT
//    @Path("{uuid}")
//    public DeviceTO update(@PathParam("uuid") UUID uuid, DeviceTO deviceTO) {
//        final DeviceEO old = deviceService.findOne(uuid);
//        old.setName(deviceTO.getName());
//        old.setDeviceType(deviceTO.getDeviceType());
//        old.setDescription(deviceTO.getDescription());
//        old.setOrganizationUuid(deviceTO.getOrganizationUuid());
//        return mapper.map(deviceService.save(old));
//    }
//
//    @DELETE
//    @Path("{uuid}")
//    public DeviceTO delete(@PathParam("uuid") UUID uuid) {
//        final DeviceEO deleted = deviceService.findOne(uuid);
//        deviceService.delete(uuid);
//        return mapper.map(deleted);
//    }
}
