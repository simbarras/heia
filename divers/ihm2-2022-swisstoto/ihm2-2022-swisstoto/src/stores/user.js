import axios from "axios";
import {ref} from 'vue'
import {defineStore} from "pinia";

export const useUserStore = defineStore('user', () => {
    const user = ref("")
    const token = ref("")

    function login() {
        const body = {
            "username": "bbdata.hmi",
            "password": "Yu3ENUtqt7kUDn"
        }

        axios.post("https://bbdata.smartlivinglab.ch/login", body)
            .then((response) => {
                user.value = response.data["userId"].toString()
                token.value = response.data["secret"]
            })
            .catch((err) => {
                console.log(err)
            })
    }

    return {user, token, login}
})