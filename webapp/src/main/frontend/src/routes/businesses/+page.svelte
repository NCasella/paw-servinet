<script lang="ts">
    import Title from "$lib/components/global/Title.svelte";
	import BigButton from "$lib/components/global/BigButton.svelte";
    import {t} from "$lib/i18n/i18n"
	import { base } from "$app/paths";
	import { onMount } from "svelte";
	import type { Business } from "$models/Business";
    import type { PagedResult } from "$models/PagedList";
	import { getUserBusinesses } from "$services/businessService";
	import Icon from "$icons";

    let loading = true
    let pagedList :PagedResult<Business> = {items: [], links: {}}
    onMount(async () => {
        pagedList = await getUserBusinesses()
            .then(r => r)
            .finally(() => loading = false);
        
    })
</script>
<header class="mx-8 flex place-content-between items-baseline">
    <Title text={$t("businesses.my-businesses")}/>
    <div class="flex justify-end l-4 space-x-3">
        <a href="{base}/create-business">
        <BigButton iconName="business" title={$t("businesses.add-business")}/>
        </a>
        <BigButton iconName="calendar" title={$t("businesses.appointments")}/>
        <BigButton iconName="notification" title={$t("businesses.questions")}/>
    </div>
</header>

{#if !loading}
<div class="boxes-container">
    {#each pagedList.items as b }
        <div class="business-container">
            <a href="{base}/business/{b.businessId}">
                <p>{b.businessName}</p>
            </a>
            <a href="{base}/business/{b.businessId}/create-service">
                <Icon name="add"/>
            </a>
        </div>
    {/each}

</div>
{/if}