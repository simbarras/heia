<script setup>
import { ref, computed } from 'vue'
import { storeToRefs } from 'pinia'
import { useSensorsStore} from "@/stores/sensors";
import { onMounted } from 'vue';

// Store instance
const sensorsStore = useSensorsStore();
const { graphicSensors } = storeToRefs(sensorsStore);

const sensors = ref(graphicSensors.value || [])
const selectedSensors = ref([])

// Graphic parameters
const fromPeriod = ref(new Date('2023-01-01').toISOString().split('T')[0])
const toPeriod = ref(new Date().toISOString().split('T')[0])


// function to generate random data for the chart

function getDates() {
  const dates = []
  const from = new Date(fromPeriod.value)
  const to = new Date(toPeriod.value)
  while (from <= to) {
    dates.push(from.toISOString().split('T')[0])
    from.setDate(from.getDate() + 1)
  }
  return dates
}

function getTemp(dates) {
  const data = []
  for (let i = 0; i < dates.length; i++) {
    data.push(Math.floor(Math.random() * 20))
  }
  return data
}

// Chart data and options
const dates = ref([])

const series = ref([{
  name: 'Temperature',
  data: [],
}])

const options = ref({
  chart: {
    height: 350,
    type: '',
  },
  plotOptions: {
    bar: {
      horizontal: false,
    },
    line: {
      curve: 'smooth',
    },
  },
  dataLabels: {
    enabled: false,
  },
  xaxis: {
    categories: dates,
  },
})

onMounted(() => {
  if(dates.value.length === 0) {
    const dates = getDates()
    const data = getTemp(dates)
    series.value[0].data = data
    dates.value = dates
  }
})
</script>


<template>
  <div class="v-row">
    <v-col cols="6">
      <div class="text-h5">Sensors data graphic</div>
      <div class="text-subtitle-1">Here you can select sensors to display measures in the graphic</div>
      <v-combobox
        v-model="selectedSensors"
        :items="sensors"
        label="sensor"
        messages="Select sensors to display measures in the graphic"
        multiple
        chips
        class="my-4"
      ></v-combobox>
    </v-col>
    <v-col cols="6">
      <div class="text-h6">Graphic options</div>
      
      <div class="text-subtitle-1">Select period of measurements</div>
      <input type="date" class="v-col-6" v-model="fromPeriod">
      <input type="date" class="v-col-6" v-model="toPeriod">
      
      <v-select
        v-model="options.chart.type"
        :items="['line', 'bar', 'area', 'histogram', 'pie', 'donut', 'radialBar', 'scatter', 'bubble', 'heatmap']"
        label="Chart type"
        class="v-col-6"
      />
    </v-col>
  </div>
  <apexchart height="100%" :type="options.chart.type" :options="options" :series="series"></apexchart>
</template>