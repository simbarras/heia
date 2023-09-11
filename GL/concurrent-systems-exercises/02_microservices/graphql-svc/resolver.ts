import { Device, DeviceInput, Organization, ParamRequest } from "../lib/models";
import * as ctrl from '../lib/controller';

/**
 * 
 * GraphQL:
{
  devices(after:"name1", pageSize: 2) {
    name
    uuid
    createdAt
  }
}
 */
const devices = async (param: ParamRequest): Promise<Device[]> => {
  const { after, pageSize } = param;
  console.log(`Devices requested (after: ${after} - pageSize: ${pageSize})`);
  return ctrl.devices(after, pageSize);
}
/**
 * GraphQL:
{
  organizations {
    createdAt
    name
    uuid
  }
}
 * @returns list des organizations
 */
const organizations = async (): Promise<Organization[]> => {
  console.log('Organizations requested');
  return ctrl.organizations();
}

/**
 * create new organization
 * GraphQL:
mutation {
  createOrganization(createRequest: {name: "test"}) {
    uuid
    name
    createdAt
  }
}
 * @param name of the new organization
 */
const createOrganization = async (param: ParamRequest): Promise<Organization> => {
  const name = param.createRequest.name;
  console.log(`Create organization requested (name: ${param.createRequest.name})`);
  return ctrl.createOrganization(name);
}

/**
 * 
mutation {
  deleteOrganization(deleteRequest: {name: "test"}) {
    uuid
    name
    createdAt
  }
}
 * @param param 
 * @returns 
 */
const deleteOrganization = async (param: ParamRequest): Promise<Organization> => {
  const name = param.deleteRequest.name;
  console.log(`Delete organization requested (name: ${name})`);
  return ctrl.deleteOrganization(name);
}


/**
 * 
mutation {
  saveDevice(request: {description: "description", deviceType: RAIN, name: "name", organizationUuid: "uuid-orgTest"}) {
    createdAt
    description
    deviceType
    modifiedAt
    name
    organization {
      name
      uuid
      createdAt
    }
    uuid
  }
}
 * @param param 
 * @returns 
 */
const saveDevice = async (param: ParamRequest): Promise<Device> => {
  const d: DeviceInput = { ...param.request };
  console.log(`Save device requested (name: ${d})`);
  return ctrl.saveDevice(d);
}

/**
 * 
 * GraphQL:
mutation {
  deleteDevice(uuid: "uuid-devTest") {
    createdAt
    description
    deviceType
    modifiedAt
    name
    organization {
      name
      uuid
      createdAt
    }
    uuid
  }
}
 */
const deleteDevice = async (param: ParamRequest): Promise<Device> => {
  const uuid = param.uuid;
  console.log(`Delete device requested (uuid: ${uuid})`);
  return ctrl.deleteDevice(uuid);
}


export const root = {
  organizations,
  createOrganization,
  deleteOrganization,
  devices,
  saveDevice,
  deleteDevice,
}
