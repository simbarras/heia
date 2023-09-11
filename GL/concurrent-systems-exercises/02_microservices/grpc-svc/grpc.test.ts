import {expect} from "chai";
import * as service from "./services";
import {DeviceType} from "../lib/models";

let uuid_org: string;
let uuid_device: string;

describe('GRPC', () => {
    it('should return the list of organizations', async () => {
        const call = null;
        const callback = (status: number, response: any) => {
            console.log(response);
            expect(response.organizations).to.be.an('array');
        }
        await service.listOrganization(call, callback)
    });

    it('should create a new organization', async () => {
        const call = {
            request: {
                "name": 'unitTest organization (grpc)'
            }
        }
        const callback = (status: number, response: any) => {
            console.log('status: ' + status);
            expect(status).to.be.equal(0);
            uuid_org = response.organization.uuid;
            console.log(uuid_org);
            expect(response.organization).to.be.an('object');
            expect(response.organization.name).to.be.equal('unitTest organization (grpc)');
        }
        await service.createOrganization(call, callback)
    });

    // Add the same organization again (should fail)
    it('should fail to create a new organization', async () => {
        const call = {
            request: {
                "name": 'unitTest organization (grpc)'
            }
        }
        const callback = (err: any, _: any) => {
            // status 6 = ALREADY_EXISTS
            console.log('status: ' + err.status.toString());
            expect(err.status).to.be.equal(6);
        }
        await service.createOrganization(call, callback)
    });

    it('should create a new device', async () => {
        const call = {
            request: {
                "description": {
                    "value": "unittest description"
                },
                "device_type": DeviceType.RAIN,
                "name": "unitTest dev (grpc)",
                "organization_uuid": uuid_org
            }
        }

        const callback = (status: number, response: any) => {
            console.log(response);
            expect(response.device).to.be.an('object');
            uuid_device = response.device.uuid;
            console.log(uuid_device);
        }
        await service.saveDevice(call, callback)
    });

    // Add the same device again (should fail)
    it('should fail to create a new device', async () => {
        const call = {
            request: {
                "description": {
                    "value": "unittest description"
                },
                "device_type": DeviceType.RAIN,
                "name": "unitTest dev (grpc)",
                "organization_uuid": uuid_org
            }
        }

        const callback = (err: any, _: any) => {
            // status 6 = ALREADY_EXISTS
            expect(err.status).to.be.equal(6);
        }

        await service.saveDevice(call, callback)

    });

    it('should return the list of devices', async () => {
        const call = {
            request: {
                "after": "unitTest dev (grpc)",
            }
        }
        const callback = (status: number, response: any) => {
            expect(response.devices).to.be.an('array');
        }
        await service.listDevice(call, callback)

    });

    it('should delete a device', async () => {
        const call = {
            request: {
                "uuid": uuid_device
            }
        }
        const callback = async (status: number, response: any) => {
            expect(status).to.be.equal(0);
            const call2 = {};
            const callback2 = (status: number, response: any) => {
                response.devices.forEach((device: any) => {
                    expect(device.uuid).to.not.be.equal(uuid_device);
                });
            }
            await service.listDevice(call2, callback2)
        }
        await service.deleteDevice(call, callback)


    });

    it('should delete an organization', async () => {
        const call = {
            request: {
                "name": "unitTest organization (grpc)"
            }
        }
        const callback = async (status: number, response: any) => {
            expect(status).to.be.equal(0);
            const call2 = {};
            const callback2 = (status: number, response: any) => {
                response.organizations.forEach((org: any) => {
                    expect(org.name).to.not.be.equal("unitTest organization (grpc)");
                });
            }
            await service.listOrganization(call2, callback2)
        }
        await service.deleteOrganization(call, callback)
    });
});
