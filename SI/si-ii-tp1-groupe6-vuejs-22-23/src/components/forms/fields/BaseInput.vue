<template>
  <div class="form-group py-2">
    <label class="mb-1">{{ label }}</label>
    <input
      :type="inputType"
      class="form-control"
      :class="{ 'is-invalid': errors.length >= 1, 'is-valid': isValidData }"
      :placeholder="label"
      @input="inputHandler"
      v-bind="$attrs"
      :value="$attrs.modelValue"
    />
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "BaseInput",
  props: {
    label: {
      type: String,
      default: null,
    },
    errors: {
      type: Array,
      default: () => [],
    },
    isValidData: {
      type: Boolean,
      default: false,
    },
    inputType: {
      type: String,
      default: "text",
    },
  },
  methods: {
    inputHandler(e: Event) {
      const target = e.target as HTMLInputElement;
      this.$emit("update:modelValue", target.value);
    },
  },
});
</script>

<style lang="scss">
@import "@/assets/scss/vars";

</style>
