<template>
  <div class="form-label">
    <h5>Game</h5>
  </div>
  <div class="form-container p-3 my-3">
    <div class="form-title mb-2">Create Game</div>
    <form @submit.prevent="submitGame">
      <BaseInput
        label="Home team"
        v-model="gamePost.homeTeam"
        input-type="text"
      />
      <BaseInput
        label="Away team"
        v-model="gamePost.awayTeam"
        input-type="text"
      />
      <BaseInput label="Game day" v-model="gamePost.date" input-type="date" />
      <BaseInput
        label="Game location"
        v-model="gamePost.location"
        input-type="text"
      />
      <BaseSelect
        label="League"
        v-model.number="gamePost.leagueid"
        :options="
          leagues.map((league) => ({
            id: league.id,
            text: league.name,
          }))
        "
      />
      <button
        type="submit"
        id="submit-game-btn"
        class="btn btn-primary submit-btn"
      >
        Create
      </button>
    </form>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import BaseInput from "@/components/forms/fields/BaseInput.vue";
import BaseSelect from "@/components/forms/fields/BaseSelect.vue";
import { mapActions, mapState } from "pinia";
import { useStore } from "@/stores";
import type { League } from "@/stores/types";
import axios from "axios";

export default defineComponent({
  components: {
    BaseSelect,
    BaseInput,
  },
  data() {
    return {
      gamePost: {
        homeTeam: "",
        awayTeam: "",
        date: "",
        location: "",
        leagueid: -1,
      },
    };
  },
  methods: {
    ...mapActions(useStore, ["fetchLeagues"]),
    submitGame() {
      const requestOptions = {
        headers: {
          "Content-Type": "application/json",
          "Access-Control-Allow-Origin": "*",
        },
      };
      console.log(this.gamePost);
      axios.post(`/api/${this.backend}/game/add`, this.gamePost, requestOptions).then((res) => {
        console.log(res);
        if (res.status === 200) {
          this.$router.push({ name: "home" });
        }
      });
    },
  },
  computed: {
    ...mapState(useStore, ["getLeagues", "getBackend"]),
    leagues(): League[] {
      return this.getLeagues;
    },
    backend(): string {
      return this.getBackend;
    },
  },
  mounted() {
    this.fetchLeagues();
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

#submit-game-btn {
  background-color: $color-yellow;
  border: none;
  color: $color-black;
  font-size: $fs-18;
  font-weight: $fw-semibold;
}
</style>
