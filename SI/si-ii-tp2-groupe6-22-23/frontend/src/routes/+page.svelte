<script lang="ts">
   import type { PageData } from './$types';
   import ShopCatalog from './components/ShopCatalog.svelte';
   import RatingOverview from './components/RatingOverview.svelte';
   import BeerForm from './components/BeerForm.svelte';
   import type { Beer, Rating } from '../types';

   export let data: PageData;

   let editBeer: Beer;
   let ratedBeer: Beer;
   let dataRatings: Rating[] = [
    {
        "id": 1232002850,
        "drinkId": 2596996162,
        "date": "2022-12-20",
        "author": "Simon",
        "title": "Gool",
        "comment": "Go c'est gool",
        "rating": 5
    },
    {
        "id": 2324110362,
        "drinkId": 2596996162,
        "date": "2023-01-12 22:05:48.0356254 +0000 UTC",
        "author": "Siuuuuuuuumon",
        "title": "C'est bon",
        "comment": "Rounald d'eau",
        "rating": 4
    },
    {
        "id": 1347534728,
        "drinkId": 2596996162,
        "date": "2023-01-17 17:15:14.9099018 +0000 UTC",
        "author": "string",
        "title": "Cénul",
        "comment": "string",
        "rating": 1
    }
];

   function editProduct(beer: Beer) {
      editBeer = beer;
   }

   function rateProduct(beer: Beer) {
      ratedBeer = beer;
      if(beer.id) {
         console.log("fetch ratings for beer: " + beer.id);
         const ratings = fetchRatings(beer.id);
      }
   }

   async function fetchRatings(beerId: number): Promise<void> {
      console.log("fetching ratings");
      const response = await fetch('/api', 
         { 
            method: 'POST',
            headers: {
               'Content-Type': 'application/json'
            },
            body: JSON.stringify({ id: beerId })
         }
      ).then(res => res.json());

      dataRatings = response;
   }


   $: products = data.products;
</script>

<ShopCatalog products={products} edit={editProduct} rate={rateProduct} />
<div class="flex">
   <div class="basis-1/2">
      <RatingOverview ratedBeer={ratedBeer} />
   </div>
   <div class="basis-1/2">
      <BeerForm beer={editBeer} />
   </div>
</div>
<div>
   <h2 class="text-2xl font-bold">Ratings</h2>
   <div class="w-1/4">
   {#if dataRatings && dataRatings.length > 0}
      {#each dataRatings as rating}
         <div class="flex flex-col my-4">
            <div class="flex flex-row">
               <div class="basis-1/2">
                  <h3 class="text-xs">{rating.author}</h3>
               </div>
               <div class="basis-1/2">
                  <h3 class="text-xs">{new Date(rating.date).toDateString()}</h3>
               </div>
            </div>
            <div class="flex flex-row">
               <div class="basis-1/2">
                  <h3 class="text-sm font-bold">{rating.title}</h3>
               </div>
            </div>
            <div class="flex flex-row">
               <div>
                  <h3 class="text-sm font-semibold">{rating.comment}</h3>
               </div>
            </div>
            <div class="basis-1/2 flex">
               {#each Array(5) as _, i}
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="w-4 h-4 hover:fill-amber-400 cursor-pointer" class:fill-amber-400={i <= rating.rating-1 } class:fill-slate-300={i > rating.rating-1 }> 
                     <path stroke-linecap="round" stroke-linejoin="round" d="M11.48 3.499a.562.562 0 011.04 0l2.125 5.111a.563.563 0 00.475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 00-.182.557l1.285 5.385a.562.562 0 01-.84.61l-4.725-2.885a.563.563 0 00-.586 0L6.982 20.54a.562.562 0 01-.84-.61l1.285-5.386a.562.562 0 00-.182-.557l-4.204-3.602a.563.563 0 01.321-.988l5.518-.442a.563.563 0 00.475-.345L11.48 3.5z" />
                  </svg>
               {/each}
            </div>
         </div>
      {/each}
   {/if}
   </div>
</div>