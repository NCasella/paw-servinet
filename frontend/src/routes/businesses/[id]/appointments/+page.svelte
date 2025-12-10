<script lang="ts">
	import { page } from "$app/state";
	import AppoinmentList from "$lib/components/appointment/AppoinmentList.svelte";
	import { getParamIdFromUrl } from "$lib/navigation/pageInfo";
	import type { Business } from "$models/Business";
	import { AppointmentView } from "$models/enums/AppointmentStatus";
	import { getBusinessById } from "$services/businessService";
	import { onMount } from "svelte";

    let business :Business
    onMount( async () => {
    
    const businessId = getParamIdFromUrl()
    business = await getBusinessById(businessId)
    })
    
</script>
{#if business}
<AppoinmentList view={AppointmentView.BUSINESS} business={business} />
{/if}