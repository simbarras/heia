import {ref} from 'vue'
import {defineStore} from 'pinia'
import {getApiInfo, getDbInfo, setDbVersion} from "@/service/myApi";
import {getWebAppVersion} from "@/service/envVariables";

const api_info = ref("")
const db_info = ref("")
const webapp_info = ref("")

export const useInfosStore = defineStore('infos', () => {

    getDbInfo().then((value) => {
        db_info.value = value
    })
    getApiInfo().then((value) => {
        api_info.value = value
    })
    getWebAppVersion().then((value) => {
        webapp_info.value = value
    })

    return {api_info, db_info, webapp_info}
})

export const setDBStore = (version: string) => {
    setDbVersion(version).then((value) => {
        db_info.value = value;
    })
}