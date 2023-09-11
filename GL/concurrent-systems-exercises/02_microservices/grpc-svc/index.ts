import * as grpc from "@grpc/grpc-js";
import { loadSync } from "@grpc/proto-loader";
import * as svc from "./services"
import * as dotenv from "dotenv"
import { HealthCheck } from "./healthCheck";

const server = new grpc.Server();
dotenv.config()
const port = process.env.PORT
const addr = `0.0.0.0:3000`
const PROTO_PATH = __dirname + "/schema.proto";
const HEALTH_CHECK_PATH = __dirname + "/health.proto";
const healthCheck = new HealthCheck();

// HEALTH CHECK
const healthDefinition = loadSync(HEALTH_CHECK_PATH, {
    keepCase: true,
    longs: String,
    enums: String,
    defaults: true,
    oneofs: true
});
const healthProtoDescriptor: any = (grpc.loadPackageDefinition(healthDefinition) as any).grpc.health.v1;

server.addService(healthProtoDescriptor.Health.service, {
    check: healthCheck.check.bind(healthCheck),
});
healthCheck.notReady();

// Package definition
const packageDefinition = loadSync(PROTO_PATH, {
  keepCase: true,
  longs: String,
  enums: String,
  defaults: true,
  oneofs: true
});
const protoDescriptor: any = (grpc.loadPackageDefinition(packageDefinition) as any).ch.eiafr.microservice.v1;

server.addService(protoDescriptor.OrganizationService.service, {
  ListOrganization: svc.listOrganization,
  CreateOrganization: svc.createOrganization,
  DeleteOrganization: svc.deleteOrganization
});

server.addService(protoDescriptor.DeviceService.service, {
  ListDevice: svc.listDevice,
  SaveDevice: svc.saveDevice,
  DeleteDevice: svc.deleteDevice
});

// Server
server.bindAsync(addr, grpc.ServerCredentials.createInsecure(), () => {
  server.start();
});

console.log(`Run gRPC server on ${addr}`);
healthCheck.ready();