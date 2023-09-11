import {ref} from 'vue'
import {defineStore} from 'pinia'
import {getApiInfo, getDbInfo, setDbVersion} from "@/service/myApi";
import {getWebAppVersion} from "@/service/envVariables";

const text = ref("")
const snackbar = ref(false)

export const useErrorsStore = defineStore('errors', () => {
    const alertUser = (message: string) => {
        text.value = message
        snackbar.value = true
    }

    return {text, snackbar, alertUser}
})