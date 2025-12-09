<script lang="ts">
    import { onMount } from 'svelte';
    import type {PagedResult} from "$models/PagedList";
    import type {Service} from "$models/Service";
    import {getServices} from "$services/serviceService";
    import ServiceCard from "$lib/components/services/ServiceCard.svelte";
    import { Pagination } from '@skeletonlabs/skeleton-svelte';
    import Icon from "$icons";
    import Spinner from "$lib/components/global/Spinner.svelte";
    import {Categories, CategoriesInfo} from "$models/enums/CategoryType";
    import {t} from "$lib/i18n/i18n"
    import {Ratings, RatingsInfo} from "$models/enums/Rating";

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

    const categoryList = Object.values(Categories);

    $: lastPage = pagedList.links.last ?? 1;

    $: pages = (() => {
        const last = lastPage;
        if(last <= 5) {
            return Array.from({ length: last }, (_, i) => i + 1);
        }
        else if (page <= 2 || page === last) {
            return [1, 2, 'ellipsis', last];
        } else {
            return [1, 'ellipsis', page-1, page, page+1, 'ellipsis', last];
        }
    })();
</script>

{#if loading}
    <Spinner/>
{:else}
    <div class="flex gap-6">
        {#if pagedList.items.length === 0}
            <div class="flex-1 flex items-center justify-center">
                <p class="text-gray-500 text-lg">{$t("service.empty-services")}</p>
            </div>
        {:else}
            <div class="services-container">
                {#each pagedList.items as service}
                    <div class="service-card">
                        <ServiceCard {service} />
                    </div>
                {/each}
            </div>
        {/if}

        <div class="p-4 rounded-xl shadow-sm bg-primary-200 w-64 flex-shrink-0">
            <h4 class="font-semibold mb-2">{$t('services.filter-rate')}</h4>
            {#each Object.values(Ratings) as r}
                <button
                        class="flex items-center gap-2 w-full text-left p-2 rounded hover:bg-primary-100"
                        on:click={() => {
                        params.rating = RatingsInfo[r].minValue;
                        page = 1;
                        loadServices();
                    }}
                >
                    <span>{$t(RatingsInfo[r].codeMsg)}</span>
                    <span class="flex gap-0.5 text-yellow-400">
                        {#each Array(RatingsInfo[r].minValue) as _}
                            <Icon name="star"/>
                        {/each}
                    </span>
                </button>
            {/each}
            <h4 class="font-semibold mb-2 mt-5">{$t('services.filter-category')}</h4>
            {#each categoryList as c}
                <button
                    class="flex items-center gap-2 w-full text-left p-2 rounded hover:bg-primary-100"
                    on:click={() => {
                        params.category = c;
                        page = 1;
                        loadServices();
                    }}
                >
                    <Icon name={CategoriesInfo[c].icon}/>
                    <span>{$t(CategoriesInfo[c].codeMsg)}</span>
                </button>
            {/each}
        </div>
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
