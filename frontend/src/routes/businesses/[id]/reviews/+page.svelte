<script lang="ts">
	import NoResults from "$lib/components/global/pagedResults/NoResults.svelte";
    import Spinner from "$lib/components/global/Spinner.svelte";
	import ReviewsBars from "$lib/components/services/ReviewsBars.svelte";
	import { getParamIdFromUrl } from "$lib/navigation/pageInfo";
	import type { Review } from "$models/Review";
	import { getBusinessReviews } from "$services/businessService";
	import { getServiceReviews } from "$services/reviewsService";
	import { onMount } from "svelte";
    
    let reviews :Review[]
    let loading = true
    onMount( async () => {
        try {
        let businessId = getParamIdFromUrl()

        reviews = await getBusinessReviews(businessId)
    } finally{
         loading = false
    }
    })
</script>
{#if loading}
<Spinner/>
{:else if reviews}
<ReviewsBars reviews={reviews}/>
{:else}
<NoResults/>
{/if}