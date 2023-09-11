import * as ctrl from '../lib/controller';
import * as grpc from '@grpc/grpc-js';
import { Device, DeviceGrpcResponse, DeviceInput, Organization, OrganizationGrpcResponse } from '../lib/models';
import { NotFoundError, AlreadyExistsError, InvalidArgumentError } from '../lib/errors';

// ORGANIZATIONS
export const listOrganization = async (call: any, callback: any) => {
    console.log('Organizations requested');
    try {
        const organizations = await ctrl.organizations();
        callback(grpc.status.OK, { organizations: organizations.map(organizationConverter) });
    } catch (err) {
        manageError(callback, err);
    }

}

export const createOrganization = async (call: any, callback: any) => {
    const name = call.request.name;
    console.log(`Create organization requested (name: ${name})`);
    try {
        const organization = await ctrl.createOrganization(name);
        callback(grpc.status.OK, { organization: organizationConverter(organization) });
    } catch (err) {
        manageError(callback, err);
    }
}

export const deleteOrganization = async (call: any, callback: any) => {
    const name = call.request.name;
    console.log(`Delete organization requested (name: ${name})`);
    try {
        await ctrl.deleteOrganization(name);
        callback(grpc.status.OK, { google_protobuf_empty: {} });
    } catch (err) {
        manageError(callback, err);
    }
}


// DEVICES
export const listDevice = async (call: any, callback: any) => {
    const { after, pageSize } = call.request;
    console.log(`Devices requested (after: ${after?.value} - pageSize: ${pageSize})`);
    try {
        const devices = await ctrl.devices(after?.value, pageSize);
        callback(grpc.status.OK, { devices: devices.map(d => deviceConverter(d)) });
    } catch (err) {
        manageError(callback, err);
    }
}

export const saveDevice = async (call: any, callback: any) => {
    try {
        const deviceInput: DeviceInput = {
            name: call.request.name,
            organizationUuid: call.request.organization_uuid,
            uuid: call.request.uuid?.value ?? undefined,
            deviceType: call.request.device_type,
            description: call.request.description
        }

        console.log(`Save device requested (name: ${deviceInput.name})`);
        const device = await ctrl.saveDevice(deviceInput);
        callback(grpc.status.OK, { device: deviceConverter(device) });
    } catch (err) {
        manageError(callback, err);
    }
}

export const deleteDevice = async (call: any, callback: any) => {
    const uuid = call.request.uuid;
    console.log(`Delete device requested (uuid: ${uuid})`);
    try {
        await ctrl.deleteDevice(call.request.uuid);
        callback(grpc.status.OK, { google_protobuf_empty: {} });
    } catch (err) {
        manageError(callback, err);
    }
}

// TOOLS
function deviceConverter(d: Device): DeviceGrpcResponse {
    return {
        uuid: d.uuid,
        organization_uuid: d.organization.uuid,
        name: d.name,
        device_type: d.deviceType,
        description: { value: d.description },
        created_at: {
            seconds: d.createdAt.getTime(),
            nanos: (d.createdAt.getTime() % 1000) * 1000000
        },
        modified_at: {
            seconds: d.modifiedAt.getTime(),
            nanos: (d.modifiedAt.getTime() % 1000) * 1000000
        }
    }
}

function organizationConverter(c: Organization): OrganizationGrpcResponse {
    return {
        uuid: c.uuid,
        name: c.name,
        created_at: {
            seconds: c.createdAt.getTime(),
            nanos: (c.createdAt.getTime() % 1000) * 1000000
        }
    }
}

function manageError(callback: any, err: any) {
    if (err.name === 'NotFoundError') {
        callback({ status: grpc.status.NOT_FOUND, message: err.message });
    } else if (err.name === 'AlreadyExistsError') {
        callback({ status: grpc.status.ALREADY_EXISTS, message: err.message });
    } else if (err.name === 'InvalidArgumentError') {
        callback({ status: grpc.status.INVALID_ARGUMENT, message: err.message });
    } else if (err instanceof Error) {
        callback({ status: grpc.status.INTERNAL, message: err.message });
    } else {
        callback({ status: grpc.status.UNKNOWN, message: "Unknown error" });
    }
}