<script setup>
import { ref } from 'vue';
import { storeToRefs } from 'pinia'
import { useSensorsStore} from "@/stores/sensors";
import { useUserStore } from "@/stores/user";
import SensorList from "../components/SensorList.vue";
import MiscForm from '../components/MiscForm.vue';

// Store instance
const sensorsStore = useSensorsStore();
const { sensors, tags } = storeToRefs(sensorsStore);

const userStore = useUserStore();
const { user, token} = storeToRefs(userStore);

const nbSensors = ref(sensors.value.length || 0);

const pageTitle = "Sensors management and overview"

const pageDescription = `This page allows the management of sensors that you own, i.e. that have been created by a user group for which you are the administrator.`

const btnActionlabel = "New sensor/group"

let showSensorForm = ref(false)

userStore.login()

console.log(user, token)

function registerSensor(sensor) {
  showSensorForm.value = false
  sensorsStore.registerSensor(sensor)
}
</script>

<template>
    <section class="mx-8">
      <div class="my-5">
        <div class="text-h5">{{ pageTitle }}</div>
        <div class="text-subtitle-1">{{pageDescription}}</div>
        <div class="text-h6 my-2">You currently own {{ nbSensors }} sensors.</div>
      </div>
      <div class="sensor-actions d-flex justify-end">
        <v-chip append-icon="mdi-plus-circle" size="large" color="secondary" @click="showSensorForm = true">
          {{ btnActionlabel }}
        </v-chip>
        <MiscForm :dialog="showSensorForm" :sensor="undefined" @close="showSensorForm = false" @save="registerSensor" />
      </div>
      <SensorList />
    </section>
</template>
