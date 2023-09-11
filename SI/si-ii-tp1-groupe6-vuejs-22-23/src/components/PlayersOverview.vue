<template>
  <div class="players-overview-container">
    <div
      class="players-overview-header d-flex justify-content-between align-items-end"
    >
      <label class="players-overview-title">Gamblers Overview</label>
      <div class="game-overview-actions">
        <button
          class="btn btn-create-player"
          @click="$router.push({ name: 'forms' })"
        >
          Gambler Actions
        </button>
      </div>
    </div>
    <div v-for="player in players" :key="player.id" class="border-bottom py-2">
      <div class="d-flex justify-content-between">
        <span>{{ player.firstname }} {{ player.lastname }}</span>
        <span>{{ player.favoriteTeam }}</span>
      </div>
      <div>{{ playerAge(player) + "yo" }}</div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { mapActions, mapState } from "pinia";
import { useStore } from "@/stores";
import type { Player } from "@/stores/types";

export default defineComponent({
  data() {
    return {};
  },
  methods: {
    ...mapActions(useStore, ["fetchPlayers"]),

    playerAge(player: Player): string {
      const birthdate = new Date(player.birthdate);
      const age = new Date().getFullYear() - birthdate.getFullYear();
      return `${age}`;
    },
  },
  computed: {
    ...mapState(useStore, ["getPlayers"]),

    players(): Player[] {
      return this.getPlayers;
    },
  },
  mounted() {
    this.fetchPlayers();
  },
});
</script>

<style scoped lang="scss">
@import "@/assets/scss/vars";

.players-overview-title {
  font-size: $fs-14;
  color: $color-grey-dark3;
}

.icon-player {
  font-size: $fs-18;
  color: $color-black;
}

.btn-create-player {
  background-color: $color-yellow;
  color: $color-black;
}
</style>
