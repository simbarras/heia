<script lang="ts">
   import type { Beer } from "src/types";
   
   export let beer: Beer = {
      id: 0,
      name: "",
      producer: "",
      country: "",
      alcohol: 0,
      capacity: 0,
      price: 0,
   };

   // $: ({ name, producer, country, alcohol, capacity, price, id } = beer);
</script>

<div class="text-lg font-semibold mt-6">Add beers to shop</div>
<form class="bg-white rounded-lg w-3/4 mt-4" method="post" action="?/submitBeer">
   <label class="block font-medium mb-2">
      Name
      <input name="name" class="bg-gray-200 p-2 rounded-lg w-full" type="text" placeholder="Cardinal" bind:value={beer.name} >
   </label>

   <label class="block font-medium mt-4 mb-2">
      Producer
      <input name="producer" class="bg-gray-200 p-2 rounded-lg w-full" type="text" placeholder="Feldschlösschen" bind:value={beer.producer} >
   </label>

   <label class="block font-medium mt-4 mb-2">
      Country
      <input name="country" class="bg-gray-200 p-2 rounded-lg w-full" type="text" placeholder="Switzerland" bind:value={beer.country} >
   </label>

   <label class="block font-medium mt-4 mb-2">
      Alcohol (%)
      <input name="alcohol" class="bg-gray-200 p-2 rounded-lg w-full" type="number" placeholder="4.80" step="0.01" pattern="[+]?([0-9]*[.])?[0-9]+" bind:value={beer.alcohol} >
   </label>

   <label class="block font-medium mt-4 mb-2">
      Capacity (cl)
      <input name="capacity" class="bg-gray-200 p-2 rounded-lg w-full" type="number" placeholder="33" step="0.01" pattern="[+]?([0-9]*[.])?[0-9]+" bind:value={beer.capacity} >
   </label>

   <label class="block font-medium mt-4 mb-2">
      Price (CHF)
      <input name="price" class="bg-gray-200 p-2 rounded-lg w-full" type="number" placeholder="2.15" step="0.01" pattern="[+]?([0-9]*[.])?[0-9]+" bind:value={beer.price} >
   </label>

   <label class="hidden">
      Id
      <input name="id" class="hidden" type="number" bind:value={beer.id} >
   </label>

   <button class="bg-amber-500 text-black py-2 px-4 rounded-lg mt-4 hover:bg-indigo-600">
      {#if beer.name === ""}
         Add beer to catalog
      {:else}
         Edit beer
      {/if}
   </button>
</form>