<script lang="ts">
	import { page } from "$app/stores";
	import ErrorPage from "$lib/components/global/errors/ErrorPage.svelte";
	import { error } from "@sveltejs/kit";


</script>

<ErrorPage message={"hola"} status={$page.status} />