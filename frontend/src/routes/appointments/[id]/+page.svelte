<script lang="ts">
	import Title from "$lib/components/global/Title.svelte";
	import { onMount } from "svelte";
    import {t} from '$lib/i18n/i18n'
	import AppointmentView from "$lib/components/appointment/AppointmentView.svelte";
	import type { PageData } from "./$types";

    export let data: PageData
    let {business, service, user, appointment } = data
</script>
<Title text={$t("appointment.detail")}/>
<AppointmentView business={business} service={service} user={user} appointment={appointment} />