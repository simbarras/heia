<template>
    <v-row justify="center">
        <v-col cols="12" md="8" lg="4">
            <v-card>
                <v-card-title class="text-center">Register</v-card-title>
                <v-card-text>
                    <v-form ref="form" v-model="valid">
                        <v-text-field
                                v-model="firstname"
                                :rules="firstnameRules"
                                label="Firstname"
                                required
                                clearable
                        ></v-text-field>
                        <v-text-field
                                v-model="lastname"
                                :rules="lastnameRules"
                                label="Lastname"
                                required
                                clearable
                        ></v-text-field>
                        <v-text-field
                                v-model="email"
                                :rules="emailRules"
                                label="Email"
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
                            @click="createAccount"
                            :disabled="!valid"
                    >
                        Register
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-col>
    </v-row>
</template>

<script setup>
import {ref} from "vue";
import {register} from "@/stores/login";
import myRouter from "@/router";
import {useErrorsStore} from "@/stores/error";
const errorsStore = useErrorsStore()
const alertUser = errorsStore.alertUser
const firstname = ref("");
const lastname = ref("");
const email = ref("");
const password = ref("");
const valid = ref(false);
const firstnameRules = [
    v => !!v || "firstname is required",
    v => (v && v.length >= 3) || "firstname must be at least 3 characters"
];

const lastnameRules = [
    v => !!v || "lastname is required",
    v => (v && v.length >= 3) || "lastname must be at least 3 characters"
];

const emailRules = [
    v => !!v || "Email is required",
    v => (v && v.length >= 3) || "Email must be at least 3 characters",
    v => /.+@.+\..+/.test(v) || "E-mail must be valid"
];

const passwordRules = [
    v => !!v || "Password is required",
    v => (v && v.length >= 6) || "Password must be at least 6 characters"
];

const createAccount = () => {
    register(firstname.value, lastname.value, email.value, password.value,)
        .then(() => {
            console.log("Register success");
            myRouter.push({name: "account"}).then(() => {
                console.log("Navigated to account");
                window.location.reload();
            });
        })
        .catch(err => {
            alertUser("Register failed: user already exists");
            console.log("Register failed", err);
        });
};

</script>