<script lang="ts">
	import AppoinmentList from "$lib/components/appointment/AppoinmentList.svelte";
	import { AppointmentView } from "$models/enums/AppointmentStatus";
	import type { PageData } from "./$types";
	import { t } from "$lib/i18n/i18n";

    export let data :PageData
    let { business, pageNum } = data

</script>

<svelte:head>
    <title>{$t('title.business-appointments')}</title>
</svelte:head>

{#if business}
<AppoinmentList view={AppointmentView.BUSINESS} business={business} pageNum={pageNum} />
{/if}