import {version} from "../../package.json";

export async function getWebAppVersion(): Promise<string> {
    return version;
}

export function getAPiUrl(): string{
    // Read the url from the window object
    let api_url = "http://" + window.location.hostname + "/api/v1";
    console.log("api_url: " + api_url)
    return api_url;
}