<template>
  <v-app-bar
      :elevation="2"
      :image="NavBarBgImg"
      color="primary"
      location="top"
      height="55"
      class="px-5"
      dense
  >
    <v-img
        :src="WorkshopLogo"
        alt="Workshop Logo"
        max-height="50"
        max-width="50"
        contain
        @click="$router.push('/')"
        class="cursor-pointer"
    />

    <v-app-bar-title>
      <v-toolbar-items class="ml-12">
        <div class="d-flex" style="padding-left: 10px; padding-right: 10px">
          <div class="cursor-pointer" @click="$router.push('/')">{{ appTitle }}</div>
        </div>

        <div class="d-flex" v-if="user_role === 'admin'" style="padding-left: 10px; padding-right: 10px">
          <div class="cursor-pointer" @click="$router.push('/invoice')"> Invoice</div>
        </div>
      </v-toolbar-items>
    </v-app-bar-title>
    <v-spacer>
    </v-spacer>

    <template v-slot:append>
      <p class="text-h6 font-weight-bold mr-5">
        {{ user_firstname }} {{ user_lastname }}
      </p>
      <v-btn
          v-if="$route.path === '/account'"
          :to="'/account'"
          color="amber"
          icon="mdi-account">
      </v-btn>
      <v-btn
          v-else
          :to="'/account'"
          color="white"
          icon="mdi-account">
      </v-btn>
    </template>
  </v-app-bar>
</template>

<script setup>

import WorkshopLogo from '@/assets/logo_projet_si.png';
import NavBarBgImg from '../assets/navbar-bg-img.jpg';
import {storeToRefs} from "pinia";
import {useLoginStore} from "@/stores/login";
import jwt_decode from "jwt-decode";
import {computed} from "vue";

const appTitle = "Creative Workshop"

const loginStore = useLoginStore();
const {user_firstname, user_lastname, user_role} = storeToRefs(loginStore);

</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}
</style>