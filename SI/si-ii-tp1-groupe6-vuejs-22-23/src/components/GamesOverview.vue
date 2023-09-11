<template>
  <div class="games-overview-container">
    <div class="game-overview">
      <div
        class="game-overview-header d-flex justify-content-between align-items-end"
      >
        <label class="game-overview-title">Games Overview</label>
        <div class="game-overview-actions">
          <button
            class="btn btn-create-game"
            @click="$router.push({ name: 'forms' })"
          >
            Create Game
          </button>
        </div>
      </div>
      <div v-for="game in games" :key="game.id" class="border-bottom py-2">
        <div>{{ game.homeTeam }} - {{ game.awayTeam }}</div>
        <div class="game-info">
          {{ gameDate(game) }} | {{ game.location }}
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { mapActions, mapState } from "pinia";
import { useStore } from "@/stores";
import type { Game } from "@/stores/types";

export default defineComponent({
  data() {
    return {};
  },
  methods: {
    ...mapActions(useStore, ["fetchGames"]),
    gameDate(game: Game): string {
      const date = new Date(game.date);
      return date.toDateString();
    },
  },
  computed: {
    ...mapState(useStore, ["getGames"]),
    games(): Game[] {
      return this.getGames;
    },
  },
  mounted() {
    this.fetchGames();
  },
});
</script>

<style scoped lang="scss">
@import "@/assets/scss/vars";

.league-overview-title {
  font-size: $fs-14;
  color: $color-grey-dark3;
}

h2 {
  font-size: $fs-24;
  font-weight: $fw-semibold;
}

h3 {
  font-size: $fs-18;
  font-weight: $fw-light;
}

.game-info {
  color: $color-grey;
  font-weight: $fw-light;
}

.btn-create-game {
  background-color: $color-yellow;
  color: $color-black;
}

.game-overview-title {
  font-size: $fs-14;
  color: $color-grey-dark3;
}
</style>
