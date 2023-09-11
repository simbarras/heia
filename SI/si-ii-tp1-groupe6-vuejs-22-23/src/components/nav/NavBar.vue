<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-light py-2">
    <div class="container-fluid">
      <div class="d-flex align-items-center ps-5" @click="$router.push({ name: '' })">
        <img src="@/assets/img/forza-logo.png" id="logo-icon" class="pe-3" alt="forza-icon">
      </div>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
              aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse ms-5" id="navbarSupportedContent">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item px-2">
            <router-link :class="{ active: isActive('/') }" class="nav-link" to="/">Home</router-link>
          </li>
        </ul>
      </div>
      <div class="d-flex align-items-center pe-4">
        <label class="form-check-label fw-semibold" for="flexSwitchCheckDefault">Spring API</label>
        <div class="form-check form-switch mx-3">
          <input class="form-check-input" type="checkbox" role="switch" id="flexSwitchCheckDefault" @change="switchAPI">
        </div>
        <label class="form-check-label fw-semibold" for="flexSwitchCheckDefault">Dotnet API</label>
      </div>
    </div>
  </nav>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import {mapActions} from "pinia";
import {useStore} from "@/stores";

export default defineComponent({
  data() {
    return {
      springBackend: true,
    }
  },
  methods: {
    ...mapActions(useStore, ["switchBackend"]),
    isActive(routeName: String) {
      return routeName === this.$route.path;
    },
    switchAPI() {
      this.springBackend = !this.springBackend;
      this.switchBackend(this.springBackend ? "spring" : "dotnet");
    },
  },
})

</script>

<style lang="scss">
@import "@/assets/scss/vars";

#logo-icon {
  width: auto;
  height: 40px;
}

.navbar-brand {
  font-weight: $fw-light;
}

.nav-item {
  font-size: $fs-20;
  font-weight: $fw-semibold;
}

.active {
  color: $color-yellow !important;
  font-weight: $fw-semibold !important;
}
</style>