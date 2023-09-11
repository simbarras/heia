import {computed, ref, watch} from 'vue'
import {defineStore} from 'pinia'
import {createAccount, login, setBearerToken} from "@/service/myApi";
import jwt_decode from "jwt-decode";

const user_token = ref<string>("")
const user_email = ref<string>("")
const user_firstname = ref<string>("")
const user_lastname = ref<string>("")
const user_role = ref<string>("")

export const useLoginStore = defineStore('login', () => {

    if (sessionStorage.getItem("user_token")) {
        user_token.value = sessionStorage.getItem("user_token") as string
        setBearerToken(user_token.value);
        const info = extractInfo(user_token.value)
        user_email.value = info.email
        user_firstname.value = info.firstname
        user_lastname.value = info.lastname
        user_role.value = info.role
    }

    watch(
        user_token,
        (tokenVal) => {
            setBearerToken(tokenVal);
            sessionStorage.setItem("user_token", tokenVal)
            const info = extractInfo(tokenVal)
            // @ts-ignore
            user_email.value = info.email
            // @ts-ignore
            user_firstname.value = info.firstname
            // @ts-ignore
            user_lastname.value = info.lastname
            // @ts-ignore
            user_role.value = info.role

        },
        {deep: true}
    )


    return {user_email, user_firstname, user_lastname, user_role}
})

function extractInfo(token: string) {
    if (token === "") {
        return {
            email: "",
            firstname: "",
            lastname: "",
            role: ""
        }
    }
    return {
        // @ts-ignore
        email: jwt_decode(token).sub,
        // @ts-ignore
        firstname: jwt_decode(token).firstName,
        // @ts-ignore
        lastname: jwt_decode(token).lastName,
        // @ts-ignore
        role: jwt_decode(token).role
    }
}

export const connect = async (username: string, password: string) => {
    user_token.value = await login(username, password);
}

export const register = async (firstname: string, lastname: string, email: string, password: string) => {
    user_token.value = await createAccount(firstname, lastname, email, password);
}

export const disconnect = async () => {
    user_token.value = "";
}

export const getRole = () => {
    return user_role.value;
}