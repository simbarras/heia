<script lang="ts">
   import type { PageData } from '../$types';
   import Product from './Product.svelte';
   import type { Beer } from '../../types';

   function handleClick() {}

   export let products: Beer[];
   export let edit: (beer: Beer) => void
   export let rate: (beer: Beer) => void

   // destructuring data to convert it to an array of type Beer
</script>

<div class="shop-header flex justify-between items-end py-4">
   <div class="text-slate-500">List of beers</div>
</div>
<div class="flex flex-row flex-wrap">
   {#each products as product}
      <Product beer={product} on:click={handleClick} edit={edit} rate={rate} />
   {/each}
</div>

<style>
</style>
