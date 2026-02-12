<script lang="ts">
	import AppoinmentList from "$lib/components/appointment/AppoinmentList.svelte";
	import { AppointmentView } from "$models/enums/AppointmentStatus";
	import type { PageData } from "./$types";
	import { t } from "$lib/i18n/i18n";

	export let data :PageData
	let {pageNum} = data
</script>

<svelte:head>
    <title>{$t('title.my-appointments')}</title>
</svelte:head>

<AppoinmentList view={AppointmentView.USER} businessId={-1} pageNum={pageNum}/>