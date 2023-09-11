<template>

    <h1>Account</h1>
    <v-spacer/>
    <v-sheet width="300" class="mx-5">
        <v-form>
            <v-text-field
                    label="Email"
                    v-model="user_email">
            </v-text-field>
            <v-text-field
                    label="First name"
                    v-model="user_firstname">

            </v-text-field>
            <v-text-field
                    label="Last name"
                    v-model="user_lastname">
            </v-text-field>
            <v-text-field
                    :disabled="true"
                    label="Role"
                    v-model="user_role">
            </v-text-field>
        </v-form>
        <v-btn
                variant="tonal"
                @click="logOut()"
        >
            Logout
        </v-btn>
    </v-sheet>

</template>

<script setup>
import myRouter from "@/router";
import {disconnect, useLoginStore} from "@/stores/login";
import {storeToRefs} from "pinia";

const loginStore = useLoginStore();
const {user_email, user_firstname, user_lastname, user_role} = storeToRefs(loginStore);

function logOut() {
    disconnect().then(() => {
        myRouter.push({name: "home"});
    }).then(window.location.reload);
}
</script>

<style scoped>

</style>