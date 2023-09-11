import { root } from './resolver';
import { expect } from "chai";
import { getGraphQLParams } from "express-graphql";
import { DeviceType, ParamRequest } from "../lib/models";

// Unit test for the GraphQL resolver

describe('GraphQL resolver', () => {
    let uuid_org: string;
    let uuid_device: string;
    it('should return the list of organizations', async () => {
        const organizations = await root.organizations()
        expect(organizations).to.be.an('array');
    });

    it('should create a new organization', async () => {
        let paramRequest = {
            createRequest: {
                name: 'unitTest organization (graphql)'
            }
        }
        const organization = await root.createOrganization(paramRequest as ParamRequest)
        console.log(organization)
        uuid_org = organization.uuid;
        expect(organization).to.be.an('object');
        expect(organization.name).to.be.equal('unitTest organization (graphql)');
    });

    it('should create a new device', async () => {
        let paramRequest = {
            request: {
                description: "unittest description",
                deviceType: DeviceType.RAIN,
                name: "unitTest device1 (graphql)",
                organizationUuid: uuid_org
            }
        }
        const device = await root.saveDevice(paramRequest as ParamRequest)
        uuid_device = device.uuid;
        console.log(device)
        expect(device).to.be.an('object');
        expect(device.name).to.be.equal('unitTest device1 (graphql)');
    });

    // Add the same device again (should fail)
    it('should fail to create a new device', async () => {
        let paramRequest = {
            request: {
                description: "unittest description",
                deviceType: DeviceType.RAIN,
                name: "unitTest device1 (graphql)",
                organizationUuid: uuid_org
            }
        }
        try {
            await root.saveDevice(paramRequest as ParamRequest)
        } catch (e) {
            expect(e.message).to.be.equal('Device unitTest device1 (graphql) already exists');
        }
    });

    it('should return the list of devices', async () => {
        let param = {
            pageSize: 10
        }
        const devices = await root.devices(param as ParamRequest)
        expect(devices).to.be.an('array');
    });


    it('should delete a device', async () => {

        console.log('----------' + uuid_device)
        let paramRequest = {
            uuid: uuid_device
        }
        const device = await root.deleteDevice(paramRequest as ParamRequest)
        expect(device.uuid).to.be.equal(uuid_device);
        expect(device).to.be.an('object');
        expect(device.name).to.be.equal('unitTest device1 (graphql)');
    });

    // delete the device again (should fail)
    it('should fail to delete a device', async () => {
        let paramRequest = {
            uuid: uuid_device
        }
        try {
            await root.deleteDevice(paramRequest as ParamRequest)
        } catch (e) {
            expect(e.message).to.be.equal('Device ' + uuid_device + ' not found');
        }
    });

    it('should delete an organization', async () => {
        let paramRequest = {
            deleteRequest: {
                name: "unitTest organization (graphql)"
            }
        }
        const organization = await root.deleteOrganization(paramRequest as ParamRequest)
        expect(organization).to.be.an('object');
        expect(organization.name).to.be.equal('unitTest organization (graphql)');

        // Check that the organization has been deleted
        const organizations = await root.organizations()
        expect(organizations).to.be.an('array');
    });
});

