<script setup>
import NavBarBgImg from '../assets/navbar-bg-img.jpg';
import BBDataLogo from '@/assets/logo.png';


const appTitle = "Big Building Data"
const menuItems = [
  // {title: 'Sensors', path: "/sensor"},
  {title: 'Graphic', path: "/graphic"},
]
</script>

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
      :src="BBDataLogo"
      alt="BBData Logo"
      max-height="50"
      max-width="50"
      contain
      @click="$router.push('/')"
      class="cursor-pointer"
    />

    <v-app-bar-title>
      <div class="d-flex">
        <div class="cursor-pointer" @click="$router.push('/')">{{ appTitle }}</div>
        <v-toolbar-items class="ml-12">
          <template v-for="item in menuItems">
            <v-btn
              v-if="$route.path === item.path"
              :to="item.path"
              class="font-weight-bold text-h6">
              {{ item.title }}
            </v-btn>
            <v-btn
              v-else
              :to="item.path"
              class="font-weight-black">
              {{ item.title }}
            </v-btn>
          </template>
        </v-toolbar-items>
      </div>
    </v-app-bar-title>

    <template v-slot:append>
      <v-btn
          v-if="$route.path === '/profile'"
          :to="'/profile'"
          color="amber"
          icon="mdi-account">
      </v-btn>
      <v-btn
          v-else
          :to="'/profile'"
          color="white"
          icon="mdi-account">
      </v-btn>      
    </template>
  </v-app-bar>
</template>

<style scoped>
  .cursor-pointer {
    cursor: pointer;
  }
</style>