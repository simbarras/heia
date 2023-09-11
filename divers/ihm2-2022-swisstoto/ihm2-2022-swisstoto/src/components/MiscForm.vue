<script setup>
import { ref, computed } from 'vue'
import { storeToRefs } from 'pinia'
import { useSensorsStore} from "@/stores/sensors";
// boolean dialog prop from parent
const props = defineProps(['dialog', 'sensor'])
const emit = defineEmits(['close', 'save'])

const editSensor = function(){
  const editSensor = props.sensor;
  editSensor.existOwnerGroup = props.sensor.owner || ''
  editSensor.newOwnerGroup = ''
  return editSensor
}

const sensor = ref(props.sensor ? editSensor() : {
  existOwnerGroup: '',
  newOwnerGroup: '',
  name: '',
  description: '',
  unit: '',
  group: '',
  createdAt: '',
  owner: '',
  tags: [],
})
</script>

<template>
  <v-form
    ref="form"
    v-model="valid"
    lazy-validation
  >
   <v-row justify="center">
     <v-dialog
       v-model="props.dialog"
       persistent
       max-width="100vh"
     >
       <v-card>
         <v-card-title>
           <span class="text-h6">New sensor</span>
         </v-card-title>
         <v-card-text>
            <div class="text-h6 mb-3">
               Sensor group
               <span class="text-body-1 pl-1">(choose an existing group or create a new one)</span>
            </div>
             <v-row>
               <v-col
                 cols="12"
                 sm="6"
                 md="6"
               >
                <!-- disable this field if sensor.owner is set -->
                
                 <v-text-field v-model="sensor.newOwnerGroup" v-if="sensor.existOwnerGroup === ''"
                   label="Name*"
                   messages="New sensor group with name"
                   required
                   variant="outlined"
                 ></v-text-field>
                 <v-text-field v-model="sensor.newOwnerGroup" v-else
                   label="Name*"
                   messages="New sensor group with name"
                   required
                   variant="outlined"
                   disabled
                 ></v-text-field>
               </v-col>
               <v-col
                 cols="12"
                 sm="6"
                 md="6"
               >
                 <v-select v-if="sensor.newOwnerGroup === ''"
                   :items="[{title: 'Group 1', value: 'group1'}, {title: 'Group 2', value: 'group2'}, {title: 'Group 3', value: 'group3'}]"
                   v-model="sensor.existOwnerGroup"
                   label="Ownership*"
                   messages="Choose ownership within existing groups"
                   required
                   variant="outlined"
                   clearable
                   @click:clear="sensor.existOwnerGroup = ''"
                 ></v-select>
                 <v-select v-else
                   :items="[{title: 'Group 1', value: 'group1'}, {title: 'Group 2', value: 'group2'}, {title: 'Group 3', value: 'group3'}]"
                   v-model="sensor.existOwnerGroup"
                   label="Ownership*"
                   messages="Choose ownership within existing groups"
                   required
                   variant="outlined"
                   clearable
                   disabled
                   @click:clear="sensor.existOwnerGroup = ''"
                 ></v-select>
               </v-col>
             </v-row>
            <div class="text-h6 my-3">
               Sensor
               <span class="text-body-1 pl-1">(choose an existing group or create a new one)</span>
            </div>
            <v-row class="flex-row flex-column">
               <v-col cols="12" md="6">
                  <v-text-field
                   label="Name*"
                   messages="Name"
                   required
                   v-model="sensor.name"
                 ></v-text-field>
               </v-col>
               <v-col
                 cols="12"
                 sm="6"
                 md="6"
               >
               <v-select
                   :items="['ampere', 'ampère AC', 'ampère DC', 'degree celsius C', 'free text', 'Facteur de puissance', 'Decibel', 'Direction', 'enumerate', 'hours', 'presence', 'frequence', 'GWP', 'speed[km/h]', 'kilo volt ampere', 'power kW', 'energy kWh', 'current', 'Pressure', 'Conductivity', 'precipitation[mm]', 'power mW', 'free number', 'state ON', 'state', 'pressure[Pa]', 'TVOC', 'qualité d\'air', 'Concentration', 'volts', 'tension DC (VAC)', 'tension DC (VDC)', 'power', 'irradation', 'energy Wh', 'Energy output', 'degree[°]', 'degree celsius']"
                   label="Unit*"
                   messages="Unit"
                   required
                   v-model="sensor.unit"
                   variant="solo"
                 ></v-select>
               </v-col>
               <v-col cols="12">
                 <v-textarea
                   label="Description"
                   type="text"
                   clearable
                   no-resize
                   max-rows="3"
                    v-model="sensor.description"
                 ></v-textarea>
               </v-col>
            </v-row>
            <v-row>
                <v-col cols="12">
                    <v-text-field
                    label="Tag ( ,Tag2, ...)"
                    messages="Tags (multiple tags seperated by comma)"
                    v-model="sensor.tags"
                    variant="underlined"
                  ></v-text-field>
                </v-col>
            </v-row>
            <small>*indicates required field</small>
         </v-card-text>
         <v-card-actions>
            <v-btn
               color="success"
               variant="text"
               @click="emit('save', sensor)"
            >
               Register sensor
            </v-btn>
            <v-btn
               color="error"
               variant="plain"
               @click="emit('close')"
            >
                Cancel
            </v-btn>
         </v-card-actions>
       </v-card>
     </v-dialog>
   </v-row>
  </v-form>
 </template>