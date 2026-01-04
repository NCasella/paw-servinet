<script lang="ts">
    import type { PagedResult } from "$models/PagedList";
    import type { Service } from "$models/Service";
    import { getServices } from "$services/serviceService";
    import ServiceCard from "$lib/components/services/ServiceCard.svelte";
    import { Pagination } from '@skeletonlabs/skeleton-svelte';
    import Icon from "$icons";
    import Spinner from "$lib/components/global/Spinner.svelte";
    import { Categories, CategoriesInfo } from "$models/enums/CategoryType";
    import { Ratings, RatingsInfo } from "$models/enums/Rating";
    import { t } from "$lib/i18n/i18n";

    import { page as pageStore } from '$app/stores';
    import { goto } from '$app/navigation';
    import {StatusCodes} from "$models/exceptions/statusCodesEnum";

    let loading = true;
    let page = 1;

    let pagedList: PagedResult<Service> = {
        items: [],
        links: {}
    };

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

    $: {
        const urlParams = $pageStore.url.searchParams;

        params.category = urlParams.get('category') ?? undefined;
        params.neighbourhoods = urlParams.get('neighbourhoods') ?? undefined;
        params.rating = urlParams.get('rating')
            ? Number(urlParams.get('rating'))
            : undefined;
        params.searchQuery = urlParams.get('search') ?? '';
        params.orderFilters = urlParams.get('orderFilters') ?? undefined;
        params.homeServiceFilter =
            urlParams.get('homeServiceFilter') === 'true' ? true : undefined;

        page = urlParams.get('page')
            ? Number(urlParams.get('page'))
            : 1;

        loadServices();
    }

    async function loadServices() {
        loading = true;
        params.page = page;
        try {
            pagedList = await getServices(params);
        } catch (e) {
            if(e.status === StatusCodes.BAD_REQUEST)
                console.log("Invalid Params");
        }
        loading = false;
    }

    function updateUrl() {
        const searchParams = new URLSearchParams();

        if (params.category) searchParams.set('category', params.category);
        if (params.neighbourhoods) searchParams.set('neighbourhoods', params.neighbourhoods);
        if (params.rating) searchParams.set('rating', String(params.rating));
        if (params.searchQuery) searchParams.set('search', params.searchQuery);
        if (params.orderFilters) searchParams.set('orderFilters', params.orderFilters);
        if (params.homeServiceFilter) searchParams.set('homeServiceFilter', 'true');
        if (page > 1) searchParams.set('page', String(page));

        goto(`?${searchParams.toString()}`, {replaceState: false});
    }

    function search() {
        params.category = undefined;
        params.neighbourhoods = undefined;
        params.rating = undefined;
        params.orderFilters = undefined;
        params.homeServiceFilter = undefined;
        params.page = 1;
        updateUrl();
    }

    const categoryList = Object.values(Categories);

    $: lastPage = pagedList.links.last ?? 1;

    $: pages = (() => {
        if (lastPage <= 5) {
            return Array.from({ length: lastPage }, (_, i) => i + 1);
        }
        if (page <= 2) {
            return [1, 2, 'ellipsis', lastPage];
        }
        if (page >= lastPage - 1) {
            return [1, 'ellipsis', lastPage - 1, lastPage];
        }
        return [1, 'ellipsis', page - 1, page, page + 1, 'ellipsis', lastPage];
    })();
</script>

{#if loading}
    <Spinner/>
{:else}
    <div class="flex gap-2 mb-4 w-full">
        <input
            type="text"
            placeholder={$t('services.search-placeholder')}
            bind:value={params.searchQuery}
            class="flex-1 p-2 pl-5 rounded-3xl border border-gray-300 hover:shadow-md"
            on:keydown={(e) => {
                if (e.key === 'Enter') {
                    search();
                }
            }}
        />
        <button class="px-4 rounded-3xl bg-primary-500 text-white hover:bg-primary-600 hover:shadow-md"
            on:click={() => {
                search();
            }}
        >
            <Icon name="search"/>
        </button>
        <select
            class="w-1/4 p-2 rounded-3xl bg-surface-300 cursor-pointer hover:shadow-md"
            bind:value={params.orderFilters}
            on:change={() => {
                page = 1;
                updateUrl();
            }}
        >
            <option value={undefined}>{$t('services.order-by')}</option>
            <option value="rate_desc">{$t('reviews.rating-desc')}</option>
            <option value="rate_asc">{$t('reviews.rating-asc')}</option>
        </select>
    </div>

    <div class="flex gap-6 w-full">
        {#if pagedList.items.length === 0}
            <div class="flex-1 flex items-center justify-center">
                <p class="text-gray-500 text-lg">{$t("service.empty-services")}</p>
            </div>
        {:else}
            <div class="flex-1">
                {#each pagedList.items as service}
                    <div class="service-card">
                        <ServiceCard {service} />
                    </div>
                {/each}
            </div>
        {/if}

        <div class="p-4 rounded-xl shadow-sm bg-primary-200 w-64 flex-shrink-0">
            <label class="flex items-center gap-3 mb-4 cursor-pointer">
                <span class="font-medium">{$t('services.filter-home')}</span>
                <input
                    type="checkbox"
                    class="sr-only"
                    checked={params.homeServiceFilter === true}
                    on:change={(e) => {
                        params.homeServiceFilter = e.currentTarget.checked ? true : undefined;
                        page = 1;
                        updateUrl();
                    }}
                />
                <div class="w-11 h-6 bg-gray-300 rounded-full relative transition {params.homeServiceFilter ? 'bg-primary-500' : ''}">
                    <div class="absolute top-0.5 left-0.5 w-5 h-5 bg-white rounded-full transition {params.homeServiceFilter ? 'translate-x-5' : ''}"/>
                </div>
            </label>

            <h4 class="font-semibold mb-2">{$t('services.filter-rate')}</h4>
            {#each Object.values(Ratings) as r}
                <button class={`flex items-center gap-2 w-full text-left p-2 rounded hover:bg-primary-100
                ${params.rating === RatingsInfo[r].minValue ? 'bg-primary-300' : ''}`}
                on:click={() => {
                    params.rating = params.rating === RatingsInfo[r].minValue ? undefined : RatingsInfo[r].minValue;
                    page = 1;
                    updateUrl();
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
                <button class={`flex items-center gap-2 w-full text-left p-2 rounded hover:bg-primary-100
                    ${params.category === c ? 'bg-primary-300' : ''}`}
                    on:click={() => {
                        params.category = params.category === c ? undefined : c;
                        page = 1;
                        updateUrl();
                    }}
                >
                    <Icon name={CategoriesInfo[c].icon}/>
                    <span>{$t(CategoriesInfo[c].codeMsg)}</span>
                </button>
            {/each}
        </div>
    </div>

    <div class="w-full flex justify-center my-6">
    <Pagination
        count={lastPage}
        pageSize={1}
        {page}
        onPageChange={(event) => {
            page = event.page;
            updateUrl();
        }}
        class="flex justify-center items-center space-x-2"
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
                        on:click={() => { page = p; updateUrl(); }}
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
    </div>
{/if}
