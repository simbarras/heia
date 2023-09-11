<script setup>
import {useErrorsStore} from "@/stores/error";
import {storeToRefs} from "pinia";
import NavbarCmp from "@/components/NavbarCmp.vue";
import FooterCmp from "@/components/FooterCmp.vue";

const errorsStore = useErrorsStore()
const {text, snackbar} = storeToRefs(errorsStore)

</script>

<template>
    <v-app>
        <navbarCmp/>
        <v-main>
            <v-container>
                <router-view/>
            </v-container>
        </v-main>
        <footerCmp/>
        <v-snackbar
                v-model="snackbar"
                :timeout="5000">
            {{ text }}

            <template v-slot:actions>
                <v-btn
                        color="blue"
                        variant="text"
                        @click="snackbar = false"
                >
                    Close
                </v-btn>
            </template>
        </v-snackbar>
    </v-app>
</template>