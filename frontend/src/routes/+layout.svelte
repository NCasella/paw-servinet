<script lang="ts">
	import './layout.css';

	import '../app.css';

	
	export let data : LayoutData
	import Layout from '$lib/styles/layout.svelte';
	import { navigating } from '$app/stores';
	import Spinner from '$lib/components/global/Spinner.svelte';
    import TopBar from "$lib/components/menu/TopBar.svelte";	
	import type { LayoutData } from './$types';
</script>

<div class="body" data-theme="sktheme">
	<div class="container p-4">

    <TopBar data={data}/>
  
	<main class="mt-8">
		{#if $navigating}
			<Spinner/>
		{/if}
		<slot />

	</main>

	</div>


</div>


<style>
  .container {
    max-width: 1080px;
    margin: 0 auto;
  }
</style>

