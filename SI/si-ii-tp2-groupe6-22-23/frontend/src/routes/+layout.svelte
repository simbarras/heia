<script lang="ts">
    import type { LayoutData } from './$types';
    import NavBar from './components/NavBar.svelte';
    import "../app.css";
    
    //export let data: LayoutData;
</script>

<svelte:head>
	<title>DrinkCrawl</title>
	<meta name="description" content="Drink catalog app" />
</svelte:head>

<div class="app">
    <NavBar />
    <main>
        <div class="container mx-auto">
            <slot></slot>
        </div>
    </main>
</div>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@100;200;300;400;500;600;700;800;900&display=swap');

    :root {
        font-family: 'Inter', sans-serif;
    }

    .app {
		display: flex;
		flex-direction: column;
		min-height: 100vh;
	}
</style>