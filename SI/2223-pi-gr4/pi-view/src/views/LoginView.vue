<template>
  <v-container fluid>
    <v-card>
      <v-tabs
          v-model="tab"
          bg-color="primary"
      >
        <v-tab value="one">Login</v-tab>
        <v-tab value="two">Register</v-tab>
      </v-tabs>

      <v-card-text>
        <v-window v-model="tab">
          <v-window-item value="one">
            <LoginCmp />
          </v-window-item>
          <v-window-item value="two">
            <RegisterCmp />
          </v-window-item>
        </v-window>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import LoginCmp from "@/components/LoginCmp.vue";
import {ref} from "vue";
import RegisterCmp from "@/components/RegisterCmp.vue";
const tab = ref("one");
</script>