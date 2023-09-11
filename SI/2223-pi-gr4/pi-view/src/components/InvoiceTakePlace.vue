<template>
  <v-form v-model="valid">
    <v-row>
      <v-col cols="12" sm="6" md="4">
        <div>
          <v-btn
              color="primary"
              @click="generateOccursPDF"
          >
            Générer le PDF
          </v-btn>
          <v-btn
              color="primary"
              @click="generateOccursCSV"
          >
            Générer le CSV
          </v-btn>
        </div>

      </v-col>
    </v-row>
  </v-form>

</template>

<script setup>
import {computed, reactive, ref, watch} from 'vue';
import jsPDF from 'jspdf';
import {useOccursStore} from "@/stores/occurs";
import {storeToRefs} from "pinia";

const occursStore = useOccursStore()
const {occurs_list} = storeToRefs(occursStore)

const state = reactive({
  title: 'Occurrences list that took place',
  occurrence: '',
  content: '',
});

const valid = ref(false);

// Rules for the form
const activityRules = [
  // If v is not empty
  v => !!v || 'Activity is required',
];

const generateOccursPDF = async () => {
  console.log(state.participant_list);
  const doc = new jsPDF();
  doc.setFontSize(18);
  doc.setFont('helvetica', 'bold');
  doc.text(state.title, 20, 20);
  doc.setLineWidth(0.5);
  doc.line(20, 30, 190, 30);
  doc.setFontSize(12);
  doc.setFont('helvetica', 'normal');
  for (let i = 0; i < filtered_occurs_list.value.length; i++) {
    doc.text(`${filtered_occurs_list.value[i].name} -  ${filtered_occurs_list.value[i].dateActivity.toISOString().substring(0, 10)} - ${filtered_occurs_list.value[i].price} CHF`, 20, 40 + i * 10);
  }

  doc.save(`occurrence_take_place.pdf`);
};

const generateOccursCSV = async () => {
  let csvContent = "data:text/csv;charset=utf-8," + "Name,Date,Price\n";
  for (let i = 0; i < filtered_occurs_list.value.length; i++) {
    csvContent += `${filtered_occurs_list.value[i].name},${filtered_occurs_list.value[i].dateActivity.toISOString().substring(0, 10)},${filtered_occurs_list.value[i].price}\n`;
  }
  const encodedUri = encodeURI(csvContent);
  const link = document.createElement("a");
  link.setAttribute("href", encodedUri);
  link.setAttribute("download", `occurs_list.csv`);
  document.body.appendChild(link); // Required for FF
  link.click();

};
const filtered_occurs_list = computed(() => {
  return occurs_list.value.filter(occurrence => {
    return occurrence.nbPerson >= occurrence.minPerson && occurrence.dateActivity >= new Date();
  });
});

</script>