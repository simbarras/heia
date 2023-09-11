import { buildSchema } from "graphql"
import * as express from "express"
import { graphqlHTTP } from "express-graphql"
import { root } from "./resolver"
import * as dotenv from "dotenv"

const schema = buildSchema(`
type Device {
    "ISO-8601"
    createdAt: DateTime!
    description: String
    deviceType: DeviceType!
    "ISO-8601"
    modifiedAt: DateTime
    name: String!
    organization: Organization!
    uuid: String!
}

"Mutation root"
type Mutation {
    "Create a new organization. Return the freshly created organization"
    createOrganization(createRequest: CreateOrganizationRequest!): Organization!
    "Delete a device. Return the deleted device"
    deleteDevice(uuid: String!): Device!
    "Delete an organization by name. Return the deleted organization"
    deleteOrganization(deleteRequest: DeleteOrganizationRequest!): Organization!
    "Save a device (create or update). Return the saved Device"
    saveDevice(request: SaveDeviceRequest!): Device!
}

type Organization {
    "ISO-8601"
    createdAt: DateTime
    name: String!
    uuid: String!
}

"Query root"
type Query {
    "List the devices. Simple pagination with pageSize and 'after' parameter"
    devices(after: String, pageSize: Int!): [Device!]!
    "List all organizations"
    organizations: [Organization!]!
}

enum DeviceType {
    RAIN
    TEMPERATURE_HUMIDITY
    WIND
}

"Scalar for DateTime"
scalar DateTime

input CreateOrganizationRequest {
    name: String!
}

input DeleteOrganizationRequest {
    name: String!
}

input SaveDeviceRequest {
    description: String
    deviceType: DeviceType!
    name: String!
    organizationUuid: String!
    uuid: String
}
`)

const app = express()

app.use(
    "/graphql",
    graphqlHTTP({
        schema: schema,
        rootValue: root,
        graphiql: true,
    })
)
app.use("/health", (req, res) => {
    res.send("OK")
})

dotenv.config()
const port = process.env.PORT
app.listen(port)

console.log(`Running a GraphQL API server at http://localhost:${port}/`)