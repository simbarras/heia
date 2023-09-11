<template>
  <div class="form-group py-2">
    <label class="mb-1">{{ label }}</label>
    <select
      class="form-select"
      :class="{ 'is-invalid': errors.length >= 1, 'is-valid': isValidData }"
      @change="changeHandler"
      v-bind="$attrs"
      :value="$attrs.modelValue"
    >
      <option value=""></option>
      <option
        v-for="(option, index) in options"
        :key="label + index"
        :value="option.id"
      >
        {{ option.text }}
      </option>
    </select>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "BaseSelect",
  props: {
    label: {
      type: String,
      default: null,
    },
    options: {
      type: Array as () => { id: number|undefined; text: string }[],
      default: () => [],
    },
    // preselectedValue: {
    //   type: String,
    //   default: null,
    // },
    errors: {
      type: Array,
      default: () => [],
    },
    isValidData: {
      type: Boolean,
      default: false,
    },
    optionText: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      selectedValue: "",
    };
  },
  methods: {
    changeHandler(e: Event) {
      const target = e.target as HTMLInputElement;
      this.$emit("update:modelValue", target.value);
    },
  },
});
</script>

<style lang="scss">
@import "@/assets/scss/vars";

</style>
