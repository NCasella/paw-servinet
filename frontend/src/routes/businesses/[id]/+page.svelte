<script lang="ts">
	import { base } from "$app/paths";
	import { t } from "$i18";
	import BigButton from "$lib/components/global/BigButton.svelte";
	import NoResults from "$lib/components/global/pagedResults/NoResults.svelte";
	import Spinner from "$lib/components/global/Spinner.svelte";
	import Title from "$lib/components/global/Title.svelte";
	import { getParamIdFromUrl, getPath } from "$lib/navigation/pageInfo";
	import  { type Business } from "$models/Business";
	import { getBusinessById } from "$services/businessService";
	import { onMount } from "svelte";

	let business :Business
	let loading = true
	onMount( async () => {
		try {
		const businessId = getParamIdFromUrl()
		console.log(businessId)
		business = await getBusinessById(businessId)
		} finally {
			loading = false
		}
		
	}
	)
</script>

{#if loading}
<Spinner/>
{:else if business}
<header class="mx-8 flex place-content-between items-baseline">
    <Title text={business.businessName}/>
    <div class="flex justify-end l-4 space-x-3">
        <a href={getPath(`/businesses/${business.businessId}/create-service`)}>
        <BigButton iconName="add" title={$t("business.add-service")}/>
        </a>
		<a href={getPath(`/businesses/${business.businessId}/appointments`)}>
        <BigButton iconName="calendar" title={$t("businesses.appointments")}/>
		</a>
        <BigButton iconName="notification" title={$t("businesses.questions")}/>
    </div>
</header>
{:else}
	<NoResults/>
{/if}