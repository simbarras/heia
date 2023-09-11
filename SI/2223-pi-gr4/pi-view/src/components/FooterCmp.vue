<template>
  <v-footer class="bg-grey-lighten-1">
    <v-row justify="center" no-gutters>
      <v-btn
          :key="WebApp"
          color="white"
          variant="text"
          class="mx-2"
          rounded="xl"
      >
        WebApp {{webapp_info}}
      </v-btn>
      <v-btn
          :key="Api"
          color="white"
          variant="text"
          class="mx-2"
          rounded="xl"
      >
        Api {{api_info}}
      </v-btn>
      <v-btn
          :key="DB"
          color="white"
          variant="text"
          class="mx-2"
          rounded="xl"
      >
        DB {{db_info}}
      </v-btn>

      <v-col class="text-center mt-4" cols="12">
        <small>2023 — <strong>Projet intégré (Barras Simon & Terreaux Nicolas)</strong></small>
      </v-col>
    </v-row>
  </v-footer>
</template>

<script setup>
import {useInfosStore, setDBStore} from "@/stores/infos";
import {storeToRefs} from "pinia";
import {ref} from "vue";

const infosStore = useInfosStore()
const {api_info, db_info, webapp_info} = storeToRefs(infosStore)

const db_new_info = ref('')

const sendRequest = () => {
  setDBStore(db_new_info.value)
}

</script>
