<script lang="ts">
    import Icon from "$icons";
    import Spinner from "$lib/components/global/Spinner.svelte";
    import {t} from "$lib/i18n/i18n"
    import {base} from "$app/paths";
    import {goto} from "$app/navigation";
    import {CategoriesInfo, CategoriesTypeList} from "$models/enums/CategoryType";
    import {onMount} from "svelte";
    import {page as pageStore} from "$app/stores";
    import {getServices} from "$services/serviceService";
    import ServiceCard from "$lib/components/services/ServiceCard.svelte";
    import BigButton from "$lib/components/global/BigButton.svelte";

    let loading = false;
    let pagedList = { items: [], links: {} };

    let params = {
        searchQuery: '',
        category: undefined,
    };

    let carousel: HTMLDivElement;

    onMount(async () => {
        const urlParams = $pageStore.url.searchParams;
        params.searchQuery = urlParams.get('search') ?? '';
        params.category = urlParams.get('category') ?? undefined;
        await loadServices();
    });

    async function loadServices() {
        loading = true;
        pagedList = await getServices({orderFilters: 'rate_desc', page: 1});
        loading = false;
    }

    function search() {
        goto(`${base}/services?search=${params.searchQuery}`);
    }

    function goCategory(category: string) {
        goto(`${base}/services?category=${category}`);
    }

    function scrollLeft() {
        carousel.scrollBy({ left: -300, behavior: 'smooth' });
    }

    function scrollRight() {
        carousel.scrollBy({ left: 300, behavior: 'smooth' });
    }
</script>

{#if loading}
    <Spinner/>
{:else}
<div>
    <h1 class="w-full text-center font-bold  text-2xl">{$t('servinet.slogan')}</h1>
    <div class="flex gap-2 my-10 w-full">
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
    </div>

    <div class="w-full justify-center align-middle items-center">
        <div class="relative w-full mt-6">
            <button
                class="absolute left-0 top-1/2 -translate-y-1/2 z-10 bg-white shadow rounded-full p-2 hover:bg-gray-100"
                on:click={scrollLeft}
            >
                <Icon name="leftArrow" />
            </button>
            <div
                bind:this={carousel}
                class="flex gap-4 overflow-x-auto scrollbar-hide px-10 py-2 scroll-smooth display-none"
            >
                {#each CategoriesTypeList as c}
                    <button class="flex flex-col items-center justify-center min-w-[120px] h-[110px] px-3
                        rounded-xl bg-surface-100 hover:bg-primary-200 shadow-md transition cursor-pointer"
                        on:click={() => goCategory(c)}
                    >
                        <Icon name={CategoriesInfo[c].icon} />
                        <span class="mt-2 text-sm font-medium text-center">
                        {$t(CategoriesInfo[c].codeMsg)}
                    </span>
                    </button>
                {/each}
            </div>
            <button
                class="absolute right-0 top-1/2 -translate-y-1/2 z-10 bg-white shadow rounded-full p-2 hover:bg-gray-100"
                on:click={scrollRight}
            >
                <Icon name="rightArrow" />
            </button>
        </div>
    </div>

    <p class="font-bold text-lg mt-10 mb-5">{$t('home.recommended-services')}<p/>
    <div class="flex-1">
        {#each pagedList.items as service}
            <div class="service-card">
                <ServiceCard {service} />
            </div>
        {/each}
    </div>
    <div on:click={() => goto(`${base}/services`)} class="flex justify-center my-10">
        <BigButton title={$t('home.view-all-services')}/>
    </div>
</div>
{/if}

<style>
    .scrollbar-hide {
        -ms-overflow-style: none;
        scrollbar-width: none;
    }

    .scrollbar-hide::-webkit-scrollbar {
        display: none;
    }
</style>