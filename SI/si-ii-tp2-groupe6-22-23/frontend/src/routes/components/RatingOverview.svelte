<script lang="ts">
import type { Beer, Rating } from "src/types";

let starHoveredIndex = -1;

export let ratedBeer: Beer = {
   id: 0,
   name: "",
   producer: "",
   price: 0,
   capacity: 0,
   country: "",
   alcohol: 0,
   avgRating: 0,
   cntRating: 0,
};

let userRating: Rating;

$: ({ id, name, producer, price, capacity, country, avgRating, cntRating } = ratedBeer);

let author: string = "";
let title: string = "";
let comment: string = "";
let productRating: number = 0;


function rateProduct(rate: number, ratedBeer: Beer) {
   productRating = rate;
}

function submitRating() {
   userRating = {
      author: author,
      rating: productRating,
      comment: comment,
      title: title,
      date: new Date().toISOString(),
      drinkId: id as number,
      id: 0
   };
   submitProductRating(userRating);
}

async function submitProductRating(rating: Rating) {
      const response = await fetch('/api', 
         { 
            method: 'POST',
            headers: {
               'Content-Type': 'application/json'
            },
            body: JSON.stringify(rating)
         }
      ).then(res => res.json());

      return response;
   }
</script>

<div class="rating-section my-6 w-3/4">
   <div class="text-lg font-semibold">Product information</div>
   <div class="product-infos-container">
      <div class="product-infos mt-4">
         <div class="text-md font-medium">{ price } CHF</div>
         <div class="text-md font-semibold">{ name }</div>
         <div class="text-xs font-medium">{ producer }</div>
         <div class="text-xs font-light py-1">{ capacity } cl</div>
      </div>
   </div>
   <div class="product-rating-section mt-2">
      <div class="text-md font-semibold">Rate the product</div>
      <div class="rating-container">
         <div class="ratings-container">
            <div class="py-3">
               <label class="font-medium text-sm mb-2">
                  <div>Author</div>
                  <input class="bg-gray-200 p-2 rounded-lg w-1/2" type="text" placeholder="Your name" bind:value={author} >
               </label>
            </div>
            <div>
               <label class="font-medium text-sm mb-2">
                  <div>Title</div>
                  <input class="bg-gray-200 p-2 rounded-lg w-1/2" type="text" placeholder="Title" bind:value={title} >
               </label>
            </div>
            <div class="mt-2">
               <p class="text-sm font-semibold mt-2">Add a comment</p>
               <textarea class="w-3/4 h-24 border border-gray-300 rounded-md p-2" placeholder="Your comment" bind:value={comment}></textarea>
            </div>
            <div class="text-sm font-semibold">Your rating</div>
         <div class="stars-container flex items-center">    
            {#if productRating == 0}
               {#each Array(5) as _, i}
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="w-8 h-8 hover:fill-amber-400 cursor-pointer" class:fill-amber-400={i <= starHoveredIndex } class:fill-slate-300={i > starHoveredIndex } on:mouseenter={ () => {
                     starHoveredIndex = i;
                  }} on:mouseleave={ () => {
                     starHoveredIndex = -1;
                  }} on:click={() => rateProduct(++i, ratedBeer)} on:keydown={() => {}}> 
                     <path stroke-linecap="round" stroke-linejoin="round" d="M11.48 3.499a.562.562 0 011.04 0l2.125 5.111a.563.563 0 00.475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 00-.182.557l1.285 5.385a.562.562 0 01-.84.61l-4.725-2.885a.563.563 0 00-.586 0L6.982 20.54a.562.562 0 01-.84-.61l1.285-5.386a.562.562 0 00-.182-.557l-4.204-3.602a.563.563 0 01.321-.988l5.518-.442a.563.563 0 00.475-.345L11.48 3.5z" />
                  </svg>
               {/each}
            {:else}
               {#each Array(5) as _, i}
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="w-8 h-8 hover:fill-amber-400 cursor-pointer" class:fill-amber-400={i <= productRating-1 } class:fill-slate-300={i > productRating-1 }> 
                     <path stroke-linecap="round" stroke-linejoin="round" d="M11.48 3.499a.562.562 0 011.04 0l2.125 5.111a.563.563 0 00.475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 00-.182.557l1.285 5.385a.562.562 0 01-.84.61l-4.725-2.885a.563.563 0 00-.586 0L6.982 20.54a.562.562 0 01-.84-.61l1.285-5.386a.562.562 0 00-.182-.557l-4.204-3.602a.563.563 0 01.321-.988l5.518-.442a.563.563 0 00.475-.345L11.48 3.5z" />
                  </svg>
               {/each}
            {/if}
         </div>
            <button class="bg-amber-400 text-white rounded-md p-2 mt-2" on:click={() => submitRating()}>Submit</button>
         </div>
         <!-- <div class="beer-ratings-container">
            { author }
            { rating }
            { comment }
            { title }
            { date }
            { drinkId }
            { id }
         </div>     -->
      </div>
   </div>
</div>

<style>
</style>