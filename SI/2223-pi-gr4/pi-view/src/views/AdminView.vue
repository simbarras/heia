<template>
  <h1>Admin </h1>
  <v-row>
    <h2>Activity list</h2>
    <ActivityDialog/>
  </v-row>
  <ActivityList/>
</template>

<script setup lang="ts">

import ActivityDialog from "@/components/ActivityDialog.vue";
import ActivityList from "@/components/ActivityList.vue";
</script>

