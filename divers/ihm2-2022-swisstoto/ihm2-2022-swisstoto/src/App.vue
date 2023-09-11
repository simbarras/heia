<script setup>
import NavBar from './components/NavBar.vue'

</script>

<template>
  <v-app>
    <NavBar />
    <v-main>
      <v-container>
        <router-view/>
      </v-container>      
    </v-main>
  </v-app>
</template>
