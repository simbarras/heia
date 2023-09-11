<template>
    <v-row justify="center">
        <v-col cols="12" md="8" lg="4">
            <v-card>
                <v-card-title class="text-center">Login</v-card-title>
                <v-card-text>
                    <v-form ref="form" v-model="valid">
                        <v-text-field
                                v-model="username"
                                :rules="usernameRules"
                                label="Username"
                                required
                                clearable
                        ></v-text-field>
                        <v-text-field
                                v-model="password"
                                :rules="passwordRules"
                                label="Password"
                                required
                                clearable
                                type="password"
                        ></v-text-field>
                    </v-form>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn
                            color="primary"
                            @click="login"
                            :disabled="!valid"
                    >
                        Login
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-col>
    </v-row>
</template>

<script setup>
import {ref} from "vue";
import {connect} from "@/stores/login";
import myRouter from "@/router";
import {useErrorsStore} from "@/stores/error";
import {storeToRefs} from "pinia";


const errorsStore = useErrorsStore()
const alertUser = errorsStore.alertUser

const username = ref("");
const password = ref("");
const valid = ref(false);
const usernameRules = [
    v => !!v || "Username is required",
    v => (v && v.length >= 3) || "Username must be at least 3 characters"
];
const passwordRules = [
    v => !!v || "Password is required",
    v => (v && v.length >= 6) || "Password must be at least 6 characters"
];

const login = () => {
    connect(username.value, password.value)
        .then(() => {
            console.log("Login success");
            // Reload the window
            myRouter.push({name: "home"});
        })
        .catch((e) => {
            alertUser("Login failed: " + e);
            console.log("Login failed: " + e);
            myRouter.push({name: "login"});
        })
        .then(window.location.reload);
};
</script>