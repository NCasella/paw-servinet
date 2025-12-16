<script lang="ts">
	import AppoinmentList from "$lib/components/appointment/AppoinmentList.svelte";
	import { AppointmentView } from "$models/enums/AppointmentStatus";
	import type { PageData } from "./$types";

    export let data :PageData
    let { business } = data

</script>
{#if business}
<AppoinmentList view={AppointmentView.BUSINESS} business={business} />
{/if}