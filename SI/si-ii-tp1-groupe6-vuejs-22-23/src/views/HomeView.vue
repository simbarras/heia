<script setup lang="ts">
import LeaguesOverview from "@/components/LeaguesOverview.vue";
import GamesOverview from "@/components/GamesOverview.vue";
import PlayersOverview from "@/components/PlayersOverview.vue";
</script>

<template>
  <div class="container mx-auto">
    <div class="row my-3">
      <LeaguesOverview class="col-8 px-4" />
<!--      <GamesOverview class="col-4 px-4" />-->
      <PlayersOverview class="col-4 px-4" />
    </div>
  </div>
</template>
