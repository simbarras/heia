<template>
  <div class="form-label">
    <h5>League</h5>
  </div>
  <div class="form-container p-3 my-3">
    <div class="form-title mb-2">Create League</div>
    <form @submit.prevent="addLeague">
      <BaseInput
        label="League name"
        v-model="leaguePost.name"
        input-type="text"
      />
      <button type="submit" id="submit-league-btn" class="btn submit-btn">
        Create
      </button>
    </form>
  </div>
  <div class="form-container p-3 my-3">
    <div class="form-title mb-2">Delete league</div>
    <form @submit.prevent="deleteLeague">
      <BaseSelect
        label="Select league"
        v-model.number="leagueToDelete"
        :options="
          leagues.map((league) => ({
            id: league.id,
            text: league.name,
          }))
        "
      />
      <button
        type="submit"
        id="delete-league-btn"
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
      leaguePost: {
        name: "",
      },
      leagueToDelete: -1,
    };
  },
  methods: {
    ...mapActions(useStore, ["fetchLeagues"]),
    addLeague() {
      const requestOptions = {
        headers: {
          "Access-Control-Allow-Origin": "*",
        },
      };
      console.log(this.leaguePost);
      axios
        .get(`/api/${this.backend}/league/add/${this.leaguePost.name}`, requestOptions)
        .then((res) => {
          if (res.status === 200) {
            this.$router.push({ name: "home" });
          }
          console.log(res);
        });
    },
    deleteLeague() {
      const requestOptions = {
        headers: {
          "Access-Control-Allow-Origin": "*",
        },
      };
      console.log(this.leagueToDelete);
      axios
        .get(`/api/${this.backend}/league/delete/${this.leagueToDelete}`, requestOptions)
        .then((res) => {
          if (res.status === 200) {
            this.$router.push({ name: "home" });
          }
          console.log(res);
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

.form-title {
  font-size: $fs-18;
  font-weight: $fw-semibold;
}

.form-label {
  margin-bottom: 0.2rem;
}

.submit-btn {
  background-color: $color-yellow;
  border: none;
  color: $color-black;
  font-size: $fs-16;
  font-weight: $fw-regular;
  margin-top: 1rem;
  cursor: pointer;

  &:hover {
    background-color: $color-grey-dark;
    color: $color-yellow;
  }

  &:active {
    background-color: $color-grey-dark !important;
    color: $color-yellow !important;
    font-weight: $fw-semibold;
  }
}
</style>
