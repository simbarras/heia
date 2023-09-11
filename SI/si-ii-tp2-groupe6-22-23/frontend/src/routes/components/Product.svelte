<script lang="ts">
   import beerImg from '$lib/assets/beer.webp';
   import deleteIcon from '$lib/assets/bin-icon.svg';
   import editIcon from '$lib/assets/edit-icon.svg';
   import rateIcon from '$lib/assets/rate-icon.svg';
   import type { Beer } from '../../types';

   export let beer: Beer;
   export let edit: (beer: Beer) => void;
   export let rate: (beer: Beer) => void;

   $: ({ id, name, producer, price, capacity, country, avgRating, cntRating } = beer);
   $: { console.log(beer); }

   function rateProduct(rating:number) {
      return () => {
         console.log("Product rated with " + rating + "/5 stars");
      }
   }

   async function deleteBeer() {
      console.log("delete beer");
      const response = await fetch('/api', 
         { 
            method: 'DELETE',
            headers: {
               'Content-Type': 'application/json'
            },
            body: JSON.stringify({ id: id })
         }
      ).then(res => res.json()
      .then(data => {
         console.log(data);
      }));
   }

</script>

<div class="border p-5 cursor-pointer w-1/5">
   <div class="options-container flex justify-end">
      <img class="h-5 px-1" src="{editIcon}" alt="edit" on:click={() => edit(beer)} on:keydown={() => {}} />
      <img class="h-5 px-1" src="{deleteIcon}" alt="delete" on:click={deleteBeer} on:keydown={() => {}} />
      <img class="h-5 px-1" src="{rateIcon}" alt="rate" on:click={() => rate(beer)} on:keydown={() => {}} />
   </div>
   <div class="product-infos-container">
      <img class="h-36 w-auto" src="{beerImg}" alt="beer caption" />
      <div class="product-infos mt-4">
         <div class="text-md font-medium">{ price } CHF</div>
         <div class="text-md font-semibold">{ name }</div>
         <div class="text-xs font-medium">{ producer }</div>
         <div class="text-xs font-light py-1">{ capacity } cl</div>
      </div>
   </div>
   <div class="product-rating-section mt-2">
      <div class="text-xs">Ratings</div>
      <div class="rating-container">
         <div class="stars-container flex items-center">         
            {#each Array(5) as _, i}
               <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="w-5 h-5" class:fill-amber-400={i < (avgRating ? avgRating : 0) } class:fill-slate-300={i >= (avgRating ? avgRating : 0)}>
                  <path stroke-linecap="round" stroke-linejoin="round" d="M11.48 3.499a.562.562 0 011.04 0l2.125 5.111a.563.563 0 00.475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 00-.182.557l1.285 5.385a.562.562 0 01-.84.61l-4.725-2.885a.563.563 0 00-.586 0L6.982 20.54a.562.562 0 01-.84-.61l1.285-5.386a.562.562 0 00-.182-.557l-4.204-3.602a.563.563 0 01.321-.988l5.518-.442a.563.563 0 00.475-.345L11.48 3.5z" />
               </svg>
            {/each}
            <div class="text-sm font-medium pl-1">{ cntRating }</div>
         </div>
      </div>
   </div>
</div>

<style>
 
</style>

 <!-- <button on:click|once={rateProduct(++i)}>
         <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" class="w-6 h-6 fill-slate-300 hover:fill-amber-400 cursor-pointer">
            <path stroke-linecap="round" stroke-linejoin="round" d="M11.48 3.499a.562.562 0 011.04 0l2.125 5.111a.563.563 0 00.475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 00-.182.557l1.285 5.385a.562.562 0 01-.84.61l-4.725-2.885a.563.563 0 00-.586 0L6.982 20.54a.562.562 0 01-.84-.61l1.285-5.386a.562.562 0 00-.182-.557l-4.204-3.602a.563.563 0 01.321-.988l5.518-.442a.563.563 0 00.475-.345L11.48 3.5z" />
         </svg>
      </button> -->