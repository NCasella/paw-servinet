<script lang="ts">
    import { onMount } from 'svelte';
    import type {PagedResult} from "$models/PagedList";
    import type {Service} from "$models/Service";
    import {getServices} from "$services/serviceService";
    import ServiceCard from "$lib/components/services/ServiceCard.svelte";
    import { Pagination } from '@skeletonlabs/skeleton-svelte';
    import Icon from "$icons";
    import Spinner from "$lib/components/global/Spinner.svelte";

    let loading = true;
    let page = 1;
    let pagedList: PagedResult<Service> = { items: [], links: {} };

    let params = {
        businessId: undefined,
        category: undefined,
        neighbourhoods: undefined,
        rating: undefined,
        searchQuery: '',
        orderFilters: undefined,
        homeServiceFilter: undefined,
        page: 1
    };

    onMount(async () => {
        await loadServices();
    });

    async function loadServices() {
        loading = true;
        params.page = page;
        pagedList = await getServices(params);
        loading = false;
    }

    $: lastPage = pagedList.links.last ?? 1;

    $: pages = (() => {
        const last = lastPage;
        if (page <= 2 || page === last) {
            return [1, 2, 'ellipsis', last];
        } else {
            return [1, 'ellipsis', page-1, page, page+1, 'ellipsis', last];
        }
    })();
</script>

{#if loading}
    <Spinner/>
{:else}
    <div class="services-container">
        {#each pagedList.items as service}
            <div class="service-card">
                <ServiceCard {service} />
            </div>
        {/each}
    </div>

    <Pagination
        count={lastPage}
        pageSize={1}
        {page}
        onPageChange={(event) => {
            page = event.page;
            loadServices();
        }}
        class="m-15 flex justify-center items-center space-x-2"
    >
        <Pagination.PrevTrigger>
            <Icon name="leftArrow"/>
        </Pagination.PrevTrigger>
        <div class="flex justify-center items-center space-x-2 my-4">
            {#each pages as p}
                {#if p === 'ellipsis'}
                    <span class="w-8 h-8 flex items-center justify-center">…</span>
                {:else}
                    <button
                        class={`w-8 h-8 flex items-center justify-center rounded cursor-pointer hover:shadow-lg
                        ${p === page ? 'bg-primary-500 text-white font-bold' : 'bg-gray-200 text-black'}`}
                        on:click={() => { page = p; loadServices(); }}
                    >
                        {p}
                    </button>
                {/if}
            {/each}
        </div>
        <Pagination.NextTrigger>
            <Icon name="rightArrow"/>
        </Pagination.NextTrigger>
    </Pagination>
{/if}
