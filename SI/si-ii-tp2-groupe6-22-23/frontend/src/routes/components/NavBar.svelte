<script lang="ts">
   import logo from '$lib/assets/beer-icon.svg';
   import bannerImg from '$lib/assets/banner.jpg';

   let appName = 'Beer shop';
</script>

<header>
   <div class="mx-8 flex items-center font-bold">
      <a href="/">
         <img src="{logo}" alt="logo" class="h-10 pr-1" />
      </a>
      <h1 class="text-3xl font-semibold">{appName}</h1>
   </div>
</header>
<div class="banner">
   <img src="{bannerImg}" alt="banner" />
</div>

<style scoped>
   header {
      display: flex;
      place-items: center;
      height: 4rem;
      border-bottom: 1px solid #eaeaea;
   }

   h1 {
      color: #bd591d;
   }

   .banner img{
      width: 100%;
      height: 10rem;
      object-fit: cover;
   }
</style>
