<script setup>
import { ref, computed } from "vue";
import MiscForm from "./MiscForm.vue";
import {useSensorsStore} from "../stores/sensors";

const { sensor } = defineProps(
  {
    sensor: {
      type: Object,
      required: true,
    },
  }
);

const sensorsStore = useSensorsStore();

const showEditDialog = ref(false);

const { id, description, token, unit, owner, createdAt, tags, name } = sensor;

const formattedDate = computed(() => {
  if(createdAt) {
    return new Date(createdAt).toLocaleString("en-US", {
      month: "2-digit",
      day: "2-digit",
      year: "numeric",
      hour: "numeric",
      minute: "numeric",
      hour12: true,
    });
  }
  return '';
})

function editSensor(sensor) {
  console.log(sensor)
  showEditDialog.value = false;
  sensorsStore.registerSensor(sensor);
}
</script>

<template>
  <div class="sensor-item-container d-flex justify-space-around">
    <div class="sensor-id v-col-1 text-center">
      {{ name }} (id: {{ id }})
    </div>
    <div class="sensor-description v-col-3">
      {{ description }}
    </div>
    <div class="sensor-token v-col-1">
      {{ token }}
    </div>
    <div class="sensor-unit v-col-1 text-center">
      {{ unit }}
    </div>
    <div class="sensor-owner v-col-1">
      {{ owner }}
    </div>
    <div class="sensor-created-at v-col-1">
      {{ formattedDate }}
    </div>
    <div class="sensor-tags v-col-2">
      <span v-for="tag in tags">
        <v-chip prepend-icon="mdi-label" size="small" class="mr-1 mb-2">
          {{ tag }}
        </v-chip>
      </span>
    </div>
    <div class="sensor-options v-col-1">
      <v-btn
        icon
        class="mx-1"
        color="secondary"
        size="x-small"
        @click="showEditDialog = true"
      >
        <v-icon>mdi-pencil</v-icon>
      </v-btn>
      <MiscForm
        :dialog="showEditDialog"
        :sensor="$props.sensor"
        @close="showEditDialog = false"
        @save="editSensor"
        ></MiscForm>
    </div>
  </div>
</template>

<style scoped>
</style>
