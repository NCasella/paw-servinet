<script lang="ts">
	import AppoinmentList from "$lib/components/appointment/AppoinmentList.svelte";
	import { AppointmentView } from "$models/enums/AppointmentStatus";
	import type { PageData } from "./$types";

	export let data :PageData
	let {pageNum} = data
</script>
<AppoinmentList view={AppointmentView.USER} businessId={-1} pageNum={pageNum}/>