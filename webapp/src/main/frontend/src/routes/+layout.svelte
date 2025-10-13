<h1>HOLA, soy agus</h1>

<script lang="ts">
	export const ssr = false;
	// This can be false if you're using a fallback (i.e. SPA mode)
	export const prerender = false;

	import '../app.css';
	let { children } = $props();
</script>

{@render children()}

