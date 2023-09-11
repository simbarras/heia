<template>
  <v-form v-model="valid">

    <v-row>
      <v-col>
        <v-select
            :items="occurs_list"
            v-model="state.occurrence"
            name="occurrence"
            :item-title="item => `${item.name} - ${item.dateActivity.toLocaleDateString('en-GB')}`"
            label="Occurrences"
            return-object
            single-line
            :rules="activityRules"
        >
        </v-select>

        <div>
          <v-btn
              color="primary"
              @click="generateOccursPDF"
              :disabled="!valid"

          >
            Générer le PDF
          </v-btn>
          <v-btn
              color="primary"
              @click="generateOccursCSV"
              :disabled="!valid"
          >
            Générer le CSV
          </v-btn>
        </div>

      </v-col>
    </v-row>
  </v-form>

</template>

<script setup>
import {reactive, ref, watch} from 'vue';
import jsPDF from 'jspdf';
import {useOccursStore} from "@/stores/occurs";
import {useActivityStore} from "@/stores/activity";
import {storeToRefs} from "pinia";
import {Occurs} from "@/stores/beans";

const occursStore = useOccursStore()
const {occurs_list} = storeToRefs(occursStore)
const activityStore = useActivityStore()
const getParticipantsActivity = activityStore.getParticipantsActivity

const state = reactive({
  title: 'Liste des participants',
  occurrence: '',
  content: '',
  participant_list: [],
});

const valid = ref(false);

// Rules for the form
const activityRules = [
  // If v is not empty
  v => !!v || 'Activity is required',

];

const generateOccursPDF = async () => {
  state.participant_list = await getParticipantsActivity(state.occurrence.id, state.occurrence.dateActivity);
  console.log(state.participant_list);
  const doc = new jsPDF();
  doc.setFontSize(18);
  doc.setFont('helvetica', 'bold');
  doc.text(state.title, 20, 20);
  doc.setLineWidth(0.5);
  doc.line(20, 30, 190, 30);
  doc.setFontSize(12);
  doc.setFont('helvetica', 'normal');
  doc.text(`Nom de l'activité: ${state.occurrence.name}`, 20, 40);
  doc.text(`Date: ${state.occurrence.dateActivity.toISOString().substring(0, 10)}`, 20, 50);
  doc.text(`Prix de l'atelier : ${state.occurrence.price} CHF`, 20, 70);
  doc.text(`Liste des participants :`, 20, 90);
  for (let i = 0; i < state.participant_list.length; i++) {
    doc.text(`${i + 1}. ${state.participant_list[i].firstname} ${state.participant_list[i].lastname}\t${state.participant_list[i].email}`, 30, 100 + i * 10);
  }

  doc.text(`Total des participants : ${state.participant_list.length}`, 20, 100 + state.participant_list.length * 10);
  doc.text(`Prix total : ${state.participant_list.length * state.occurrence.price} CHF`, 20, 110 + state.participant_list.length * 10);

  doc.save(`participants_${state.occurrence.name}.pdf`);
};

const generateOccursCSV = async () => {
  state.participant_list = await getParticipantsActivity(state.occurrence.id, state.occurrence.dateActivity);
  let csvContent = "data:text/csv;charset=utf-8," + "Nom de l'activité,Date,Heure,Nom,Prenom,Email,Prix\n";
  state.participant_list.forEach(participant => {
    const row = `${state.occurrence.name},${state.occurrence.dateActivity.toLocaleString("en-GB")},${participant.lastname},${participant.firstname},${participant.email}, ${state.occurrence.price}\n`;
    csvContent += row;
  });
  csvContent += `Total des participants : ${state.participant_list.length},Prix total : ${state.participant_list.length * state.occurrence.price}`;
  const encodedUri = encodeURI(csvContent);
  const link = document.createElement("a");
  link.setAttribute("href", encodedUri);
  link.setAttribute("download", `participants_${state.occurrence.name}.csv`);
  document.body.appendChild(link); // Required for FF
  link.click();

};


watch(() => state.occurrence, () => {
  state.content = `Contenu du PDF \nNom de l'activité: ${state.occurrence.name}\nDate: ${state.occurrence.dateActivity.toISOString().substring(0, 10)}`
});

</script>