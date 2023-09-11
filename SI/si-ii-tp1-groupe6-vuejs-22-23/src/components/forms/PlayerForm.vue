<template>
  <div class="form-label">
    <h5>Gambler</h5>
  </div>
  <div class="form-container p-3 my-3">
    <div class="form-title mb-2">Create Gambler</div>
    <form @submit.prevent="submitPlayer">
      <BaseInput
        label="Firstname"
        v-model="playerPost.firstname"
        input-type="text"
      />
      <BaseInput
        label="Lastname"
        v-model="playerPost.lastname"
        input-type="text"
      />
      <BaseInput
        label="Birthdate"
        v-model="playerPost.birthdate"
        input-type="date"
      />
      <BaseInput
        label="Favorite team"
        v-model="playerPost.favoriteTeam"
        input-type="text"
      />
      <button
        type="submit"
        id="submit-player-btn"
        class="btn btn-primary submit-btn"
      >
        Create
      </button>
    </form>
  </div>
  <div class="form-container p-3 my-3">
    <div class="form-title mb-2">Add gambler to League</div>
    <form @submit.prevent="addPlayerToLeague">
      <BaseSelect
        label="Select league"
        v-model.number="playerAdd.leagueid"
        :options="
          leagues.map((league) => ({
            id: league.id,
            text: league.name,
          }))
        "
        @change="getPlayersFromLeague(playerAdd.leagueid)"
      />
      <BaseSelect
        label="Select gambler"
        v-model.number="playerAdd.playerid"
        :options="
          playersNotInLeague.map((player) => ({
            id: player.id,
            text: `${player.firstname} ${player.lastname}`,
          }))
        "
      />
      <button
        type="submit"
        id="submit-league-btn"
        class="btn btn-primary submit-btn"
      >
        Add
      </button>
    </form>
  </div>
  <div class="form-container p-3 my-3">
    <div class="form-title mb-2">Delete gambler from League</div>
    <form @submit.prevent="deletePlayerFromLeague">
      <BaseSelect
        label="Select league"
        v-model.number="playerDelete.leagueid"
        :options="
          leagues.map((league) => ({
            id: league.id,
            text: league.name,
          }))
        "
        @change="getPlayersFromLeague(playerDelete.leagueid)"
      />
      <BaseSelect
        label="Select gambler"
        v-model.number="playerDelete.playerid"
        :options="
          playersInLeague.map((player) => ({
            id: player.id,
            text: `${player.firstname} ${player.lastname}`,
          }))
        "
      />
      <button
        type="submit"
        id="delete-player-btn"
        class="btn btn-primary submit-btn"
      >
        Delete
      </button>
    </form>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import BaseInput from "@/components/forms/fields/BaseInput.vue";
import BaseSelect from "@/components/forms/fields/BaseSelect.vue";
import axios from "axios";
import type { League, Player } from "@/stores/types";
import { mapActions, mapState } from "pinia";
import { useStore } from "@/stores";

export default defineComponent({
  components: {
    BaseInput,
    BaseSelect,
  },
  data() {
    return {
      playerPost: {
        firstname: "",
        lastname: "",
        birthdate: "",
        favoriteTeam: "",
      },
      playerAdd: {
        leagueid: -1,
        playerid: -1,
      },
      playerDelete: {
        leagueid: -1,
        playerid: -1,
      },
      selectedLeague: null as League | null,
    };
  },
  methods: {
    ...mapActions(useStore, [
      "fetchLeagues",
      "fetchPlayers",
      "fetchLeagueById",
    ]),
    submitPlayer() {
      const requestOptions = {
        headers: {
          "Content-Type": "application/json",
          "Access-Control-Allow-Origin": "*",
        },
      };

      axios
        .post(`/api/${this.backend}/player/add`, this.playerPost, requestOptions)
        .then((res) => {
          console.log(res);
          if (res.status === 200) {
            this.$router.push({ name: "home" });
          }
        });
    },
    addPlayerToLeague() {
      const requestOptions = {
        headers: {
          "Content-Type": "application/json",
          "Access-Control-Allow-Origin": "*",
        },
      };
      console.log(this.playerAdd);
      axios
        .post(`/api/${this.backend}/league/add/player`, this.playerAdd, requestOptions)
        .then((res) => {
          console.log(res);
          if (res.status === 200) {
            this.$router.push({ name: "home" });
          }
        });
    },
    deletePlayerFromLeague() {
      const requestOptions = {
        headers: {
          "Content-Type": "application/json",
          "Access-Control-Allow-Origin": "*",
        },
      };
      console.log(this.playerDelete);
      axios
        .post(`/api/${this.backend}/league/delete/player`, this.playerDelete, requestOptions)
        .then((res) => {
          console.log(res);
          if (res.status === 200) {
            this.$router.push({ name: "home" });
          }
        });
    },
    getPlayersFromLeague(leagueid: number) {
      this.fetchLeagueById(leagueid);
    },
  },
  computed: {
    ...mapState(useStore, ["getLeagues", "getPlayers", "getSelectedLeague", "getBackend"]),
    leagues(): League[] {
      return this.getLeagues;
    },
    players(): Player[] {
      return this.getPlayers;
    },
    playersInLeague(): Player[] {
      return this.getSelectedLeague?.players || [];
    },
    playersNotInLeague(): Player[] {
      return this.players.filter(
        (player) => !this.playersInLeague.find((p) => p.id === player.id)
      );
    },
    backend(): string {
      return this.getBackend;
    },
  },
  mounted() {
    this.fetchLeagues();
    this.fetchPlayers();
  },
});
</script>

<style lang="scss">
@import "@/assets/scss/vars";

.form-container {
  background-color: $color-grey-light2;
}

.form-label {
  margin-bottom: 0.2rem;
}

#submit-player-btn {
  background-color: $color-yellow;
  border: none;
  color: $color-black;
  font-size: $fs-18;
  font-weight: $fw-semibold;
}
</style>
