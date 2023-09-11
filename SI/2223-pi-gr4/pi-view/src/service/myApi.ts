import axios from "axios";
import {getAPiUrl} from "@/service/envVariables";
import type {Occurs} from "@/stores/beans";
import type {User} from "@/stores/beans";
import type {Tools} from "@/stores/tools";
import type {Activity, Participant} from "@/stores/beans";
import type {Consumable} from "@/stores/consumable";
import type {Category} from "@/stores/category";

const client = axios.create({
    baseURL: getAPiUrl(),
    headers: {
        "Content-Type": "application/json",
    },
});

export function setBearerToken(token: string) {
    if (token == "") {
        delete client.defaults.headers.common["Authorization"];
        return;
    }
    client.defaults.headers.common["Authorization"] = `Bearer ${token}`;
}

export async function login(username: string, password: string): Promise<string> {
    return client.post("/auth/authenticate", {email: username, password: password}).then((response) => {
        return response.data.token;
    });
}

export async function createAccount(firstname: string, lastname: string, email: string, password: string): Promise<string> {
    return client.post("/auth/register", {firstname: firstname, lastname: lastname, email: email, password: password}).then((response) => {
        return response.data.token;
    });
}

export async function getUserInfo(): Promise<User> {
    return client.get("/auth/info").then((response) => {
        return response.data;
    });
}

export async function getApiInfo(): Promise<string> {
    return client.get("/health").then((response) => {
        return response.data.value;
    });
}

export async function getDbInfo(): Promise<string> {
    return client.get("/health/db").then((response) => {
        return response.data.value;
    });
}

// Post request to set the db version with the value of the input field (body)
export async function setDbVersion(version: string): Promise<string> {
    return client.post("/health/db", {version: version}).then((response) => {
        return response.data.value;
    });
}

export async function getOccurs(): Promise<Occurs[]> {
    return client.get("/occursView").then((response) => {
        return response.data;
    });
}

export async function getTools(): Promise<Tools[]> {
    return client.get("/tools").then((response) => {
        return response.data;
    });
}

export async function getActivities(): Promise<Activity[]> {
    return client.get("/activities").then((response) => {
        return response.data;
    });
}

export async function addActivity(activity: Activity): Promise<Activity> {
    return client.post("/activity", activity).then((response) => {
        return response.data;
    });
}

export async function publishActivity(id: String): Promise<Activity> {
    id = "{\"id\": \"" + id + "\"}";
    return client.post("/publish", id).then((response) => {

        return response.data;
    });
}

export async function getConsumables(): Promise<Consumable[]> {
    return client.get("/consumables").then((response) => {
        return response.data;
    });
}


export async function getCategories(): Promise<Category[]> {
    return client.get("/categories").then((response) => {
        return response.data;
    });
}

export async function modifyActivity(activity: Activity): Promise<Activity> {
    return client.put("/activity", activity).then((response) => {
        return response.data;
    }).catch((error) => {
        if (error.response.status === 409) {
            throw error.response.data;
        }
    });
}

export async function getParticipants(id: String, date: Date): Promise<Participant[]> {
    return client.get(`/participation/${id}/${date.toISOString()}`).then((response) => {
        return response.data;
    });
}

export async function updateParticipationForUser(occurs: {
    activityId: string;
    dateOccurs: string;
}): Promise<Occurs> {
    return client.post("/participation", occurs).then((response) => {
        return response.data;
    }).catch((error) => {
        if (error.response.status === 409) {
            throw error.response.data;
        }
    });
}