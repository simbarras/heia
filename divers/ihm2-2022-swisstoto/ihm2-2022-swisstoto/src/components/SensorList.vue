<script setup>
import { ref, computed } from "vue";
import {storeToRefs} from "pinia";
import {useSensorsStore} from "@/stores/sensors";
import SensorListItem from "../components/SensorListItem.vue";


const sensorsStore = useSensorsStore();
const { sensors } = storeToRefs(sensorsStore);

const listContentLabel = "List of sensors by group"
const listLabels = ["Sensor", "Description", "Tokens", "Unit", "Owner", "Created", "Tags"]

const search = ref('')

const filteredSensors = computed(() => {
  return sensors.value.filter((sensor) => {
    return sensor.owner.toLowerCase().includes(search.value.toLowerCase())
      || sensor.name.toLowerCase().includes(search.value.toLowerCase())
      || sensor.description.toLowerCase().includes(search.value.toLowerCase())
      || sensor.tags.join().toLowerCase().includes(search.value.toLowerCase())
  })
})


</script>

<template>
  <div class="sensors-section-container">
    <div class="section-header d-flex justify-space-between align-center">
      <div class="list-label text-sm v-col-7">{{ listContentLabel }}</div>
      <v-text-field
        v-model="search"
        append-icon="mdi-magnify"
        label="Search"
        single-line
        hide-details
        density="compact"
        class="py-4"
      ></v-text-field>
    </div>
    <div class="sensors-list-container d-flex justify-space-between align-center bg-grey-darken-3 rounded-t-lg">
      <div class="v-col-1 text-center py-0">{{ listLabels[0] }}</div>
      <div class="v-col-3 py-1 text-start">{{  listLabels[1] }}</div>
      <div class="v-col-1 py-1">{{ listLabels[2] }}</div>
      <div class="v-col-1 py-1 text-center">{{ listLabels[3] }}</div>
      <div class="v-col-1 py-1">{{ listLabels[4] }}</div>
      <div class="v-col-1 py-1">{{ listLabels[5] }}</div>
      <div class="v-col-2 py-1">{{ listLabels[6] }}</div>
      <div class="v-col-1 py-1"></div>
    </div>
    <SensorListItem v-for="sensor in filteredSensors" :key="sensor.id" :sensor="sensor" />
  </div>
</template>


<style scoped>
</style>