<template>
  <div class="league-overview-container">
    <div
      class="league-overview-header d-flex justify-content-between align-items-end"
    >
      <label class="league-overview-title">Leagues Overview</label>
      <div class="league-overview-actions">
        <button
          class="btn btn-create-league"
          @click="$router.push({ name: 'forms' })"
        >
          League Actions
        </button>
        <button
          class="btn btn-create-league ms-3"
          @click="$router.push({ name: 'forms' })"
        >
          Game Actions
        </button>
      </div>
    </div>
    <div
      v-for="league in leagues"
      :key="league.id"
      class="league-container my-3 p-4"
    >
      <h2>{{ league.name }}</h2>
      <div class="row justify-content-between">
        <div class="col-6 league-games-container">
          <div class="league-games-label pb-1 mb-2">Upcoming games</div>
          <div class="league-games-overview pe-3">
            <div
              v-for="game in gamesInLeague(league.id || 0)"
              :key="game.id"
              class="py-1"
            >
              <div>{{ game.homeTeam }} - {{ game.awayTeam }}</div>
              <div class="game-info d-flex justify-content-between">
                <span>{{ gameDate(game) }}</span>
                <span>{{ game.location }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="col-5 league-players-container">
          <div class="league-players-label pb-1 mb-2">League gamblers</div>
          <div class="league-players-overview pe-3">
            <div
              v-for="player in league.players"
              :key="player.id"
              class="py-1"
            >
              <div class="d-flex justify-content-between">
                <span>{{ player.firstname }} {{ player.lastname }}</span>
                <span>{{ player.favoriteTeam }}</span>
              </div>
              <div>
                <span>{{ playerAge(player) + "yo" }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import type { League } from "@/stores/types";
import { useStore } from "@/stores";
import { mapActions, mapState } from "pinia";
import type { Game } from "@/stores/types";
import type { Player } from "@/stores/types";

export default defineComponent({
  data() {
    return {};
  },
  methods: {
    ...mapActions(useStore, ["fetchLeagues", "fetchGames", "fetchPlayers"]),
    gameDate(game: Game): string {
      const date = new Date(game.date);
      return `${date.toDateString()}`;
    },
    gamesInLeague(leagueid: number): Game[] {
      return this.getGames.filter((game) => game.leagueid === leagueid);
    },
    playerAge(player: Player): string {
      const birthdate = new Date(player.birthdate);
      const age = new Date().getFullYear() - birthdate.getFullYear();
      return `${age}`;
    },
  },
  computed: {
    ...mapState(useStore, ["getLeagues", "getGames"]),
    leagues(): League[] {
      return this.getLeagues;
    },
    games(): Game[] {
      return this.getGames;
    },
  },
  mounted() {
    this.fetchLeagues();
    this.fetchGames();
    this.fetchPlayers();
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

.btn-create-league {
  background-color: $color-yellow;
  color: $color-black;
}

.league-container {
  background-color: $color-grey-light;
  border-radius: 0.75rem;
}

.game-info {
  color: $color-grey;
  font-weight: $fw-light;
}

.league-players-container,
.league-games-container {

}

.league-players-overview,
.league-games-overview {
  max-height: 10rem;
  overflow-y: scroll;
}

.league-players-label,
.league-games-label {
  font-size: $fs-14;
  font-weight: $fw-light;
  color: $color-grey-dark2;
  border-bottom: solid 0.1rem $color-yellow;
}
</style>
