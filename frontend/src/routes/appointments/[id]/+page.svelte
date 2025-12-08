<script>
	import Title from "$lib/components/global/Title.svelte";
	import { onMount } from "svelte";
    import {t} from '$lib/i18n/i18n'

    let appointment 
    onMount(async () => {

    })

</script>
<Title text={$t("appointment.detail")}/>