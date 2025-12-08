<script lang="ts">
	import './layout.css';

	import '../app.css';

	let { children } = $props();

	import Layout from '$lib/styles/layout.svelte';
	
</script>

<div class="body" data-theme="sktheme">
	<Layout>
		{@render children()}
	</Layout>
</div>
