<script lang="ts">
	import AppoinmentList from "$lib/components/appointment/AppoinmentList.svelte";
	import { AppointmentView } from "$models/enums/AppointmentStatus";
	import type { PageData } from "./$types";

    export let data :PageData
    let { business, pageNum } = data

</script>
{#if business}
<AppoinmentList view={AppointmentView.BUSINESS} business={business} pageNum={pageNum} />
{/if}