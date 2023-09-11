import { defineStore } from "pinia";
import axios from "axios";
import type { Player, Game, League } from "@/stores/types";

const config = {
  headers: {
    "Access-Control-Allow-Origin": "*",
  },
};

export const useStore = defineStore({
  id: "mainStore",
  state: () => ({
    leagues: [] as League[],
    games: [] as Game[],
    players: [] as Player[],
    selectedLeague: null as League | null,
    backend: "spring",
  }),
  actions: {
    async fetchLeagues() {
      await axios.get(`api/${this.backend}/leagues`, config).then((res) => {
        this.leagues = res.data;
        console.log(this.leagues);
      });
    },
    async fetchGames() {
      await axios.get(`api/${this.backend}/games`, config).then((res) => {
        this.games = res.data;
        console.log(this.games);
      });
    },
    async fetchPlayers() {
      await axios.get(`api/${this.backend}/players`, config).then((res) => {
        this.players = res.data;
        console.log(this.players);
      });
    },
    async fetchLeagueById(leagueid: number) {
      console.log(`/api/league/${leagueid}`, config);
      await axios.get(`api/${this.backend}/league/${leagueid}`).then((res) => {
        this.selectedLeague = res.data;
        console.log(this.selectedLeague);
      });
    },
    switchBackend(backend: string) {
      this.backend = backend;
    },
  },
  getters: {
    getLeagues: (state) => state.leagues,
    getGames: (state) => state.games,
    getPlayers: (state) => state.players,
    getSelectedLeague: (state) => state.selectedLeague,
    getBackend: (state) => state.backend,
  },
});
