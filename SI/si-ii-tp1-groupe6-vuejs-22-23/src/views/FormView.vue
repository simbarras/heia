<script lang="ts">
import { defineComponent } from "vue";
import LeagueForm from "@/components/forms/LeagueForm.vue";
import PlayerForm from "@/components/forms/PlayerForm.vue";
import GameForm from "@/components/forms/GameForm.vue";

export default defineComponent({
  components: {
    GameForm,
    PlayerForm,
    LeagueForm,
  },
});
</script>

<template>
  <div class="container mx-auto">
    <div class="row">
      <div class="col-4 my-3">
        <LeagueForm />
      </div>
      <div class="col-4 my-3">
        <GameForm />
      </div>
      <div class="col-4 my-3">
        <PlayerForm />
      </div>
    </div>
  </div>
</template>
