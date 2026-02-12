<script lang="ts">
	import NoResults from "$lib/components/global/pagedResults/NoResults.svelte";
    import Spinner from "$lib/components/global/Spinner.svelte";
	import ReviewsBars from "$lib/components/services/ReviewsBars.svelte";
	import { getParamIdFromUrl } from "$lib/navigation/pageInfo";
	import type { Review } from "$models/Review";
	import { getBusiness, getBusinessReviews } from "$services/businessService";
	import { getServiceReviews } from "$services/reviewsService";
	import { onMount } from "svelte";
	import { t } from "$lib/i18n/i18n";

    let reviews :Review[]
    let loading = true
    let businessName = ""

    onMount( async () => {
        try {
        let businessId = getParamIdFromUrl()
        const business = await getBusiness(businessId)
        businessName = business.businessName
        reviews = await getBusinessReviews(businessId)
    } finally{
         loading = false
    }
    })
</script>

<svelte:head>
    <title>{$t('title.businessReviews', [businessName])}</title>
</svelte:head>

{#if loading}
<Spinner/>
{:else if reviews}
<ReviewsBars reviews={reviews}/>
{:else}
<NoResults/>
{/if}