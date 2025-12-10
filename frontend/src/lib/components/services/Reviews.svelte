<script lang="ts">
    import { onMount } from "svelte";
    import Icon from "$icons";
    import Spinner from "$lib/components/global/Spinner.svelte";
    import Title from "$lib/components/global/Title.svelte";
    import { Pagination } from "@skeletonlabs/skeleton-svelte";

    import type { User } from "$models/User";
    import type { Review } from "$models/Review";

    import { goto } from "$app/navigation";
    import { base } from "$app/paths";
    import { t } from "$lib/i18n/i18n";
    import {ReviewForm} from "$models/forms/ReviewCreationForm";
    import {createReview, getServiceReviews} from "$services/reviewsService";
    import {ReviewFilters, ReviewFiltersInfo} from "$models/enums/ReviewFilters";
    import ReviewsBars from "$lib/components/services/ReviewsBars.svelte";

    export let serviceId: number;
    export let isOwner: boolean = false;
    export let user: User = null;

    let reviews: Review[] = [];
    let loading = true;

    let page = 1;
    let lastPage = 1;

    let currentFilter = ReviewFilters.DATE_DESC;
    const filters = Object.values(ReviewFilters);

    let hasRated: Review | null = null;

    let newForm = new ReviewForm({ serviceId, comment: "", rating: 0 });
    let newErrors = {};

    async function loadReviews(pageNum: number) {
        loading = true;

        const response = await getServiceReviews(serviceId, currentFilter, pageNum);

        reviews = response.items;
        lastPage = response.links.last ?? 1;
        page = pageNum;

        hasRated = reviews.find(r => r.user?.id === user?.id) ?? null;

        loading = false;
    }

    function selectRating(num: number) {
        newForm.rating = num;
    }

    async function submitNewReview() {
        if (!user) goto(`${base}/login`);

        newErrors = newForm.validateReviewForm();
        if (Object.keys(newErrors).length > 0) return;

        await createReview(newForm);
        newForm = new ReviewForm({ serviceId, comment: "", rating: 0 });

        await loadReviews(page);
    }

    $: pages = (() => {
        const last = lastPage;
        if (last <= 5) return Array.from({ length: last }, (_, i) => i + 1);
        if (page <= 2) return [1, 2, 3, "ellipsis", last];
        if (page >= last - 1) return [1, "ellipsis", last - 2, last - 1, last];
        return [1, "ellipsis", page - 1, page, page + 1, "ellipsis", last];
    })();

    onMount(async () => {
        await loadReviews(page);
    });
</script>

{#if loading}
    <Spinner/>
{:else}
    <div>
        <Title text={$t("service.reviews")} />
        <ReviewsBars reviews={reviews}/>

        {#if hasRated && !isOwner}
            <p class="font-bold text-lg mb-2">{$t("service.yourreview")}</p>

            <div class="mb-4 bg-surface-100 p-4 rounded-2xl">
                <div class="flex items-center">
                    <div class="flex mr-4">
                        {#each Array(hasRated.rating) as _}
                            <div class="text-yellow-400">
                                <Icon name="star"/>
                            </div>
                        {/each}
                        {#each Array(5 - hasRated.rating) as _}
                            <div class="text-surface-400">
                                <Icon name="star"/>
                            </div>
                        {/each}
                    </div>

                    <p class="text-surface-500 ml-2">{hasRated.date}</p>
                </div>

                <p class="mt-2">{hasRated.comment}</p>
            </div>
        {/if}

        {#if !isOwner && !hasRated}
            <p class="font-bold text-lg mb-2">{$t("service.add-review")}</p>

            <div class="bg-surface-100 p-4 rounded-2xl mb-4">
                <div class="flex mb-3">
                    {#each Array(5) as _, i}
                        <div class={`cursor-pointer text-lg mr-0.5 ${i < newForm.rating ? "text-yellow-400" : "text-gray-400"}`}
                             on:click={() => selectRating(i + 1)}>
                            <Icon name="star"/>
                        </div>
                    {/each}
                </div>

                <div class="flex">
                    <textarea bind:value={newForm.comment}
                              maxlength="255"
                              class="w-3/4 p-3 rounded-xl bg-surface-200"
                              placeholder={$t("service.write-review")}/>

                    {#if newErrors.comment}
                        <p class="text-red-500 text-sm">{newErrors.comment}</p>
                    {/if}

                    <div on:click={submitNewReview}
                         class="cursor-pointer flex items-center justify-center p-2 hover:opacity-80 text-2xl text-primary-500">
                        <Icon name="send"/>
                    </div>
                </div>
            </div>
        {/if}

        {#if reviews.length > 0}
            <div class="flex items-center mb-4 mt-10">
                <h4 class="mr-auto text-lg font-bold">
                    {#if currentFilter}
                        {$t("service.reviews")}{': '}{$t(ReviewFiltersInfo[currentFilter].codeMsg)}
                    {:else}
                        {$t("service.last-reviews")}
                    {/if}
                </h4>

                <select bind:value={currentFilter}
                        on:change={() => loadReviews(1)}
                        class="bg-primary-200 p-2 rounded-xl">
                    <option value="" disabled selected>
                        {$t("reviews.order-by")}
                    </option>
                    {#each filters as f}
                        <option value={f}>
                            {$t(ReviewFiltersInfo[f].codeMsg)}
                        </option>
                    {/each}

                </select>
            </div>
        {/if}

        {#each reviews as r (r.ratingId)}
            {#if !hasRated || r.ratingId !== hasRated.ratingId}
                <div class="mb-3">
                    <div class="flex items-center">
                        <span class="text-primary-700 font-semibold mr-3">@USERNAME</span>

                        <!-- stars -->
                        <div class="flex mr-3">
                            {#each Array(r.rating) as _}
                                <div class="text-yellow-400">
                                    <Icon name="star"/>
                                </div>
                            {/each}
                            {#each Array(5 - r.rating) as _}
                                <div class="text-surface-400">
                                    <Icon name="star"/>
                                </div>
                            {/each}
                        </div>

                        <span class="text-surface-500">{r.date}</span>
                    </div>
                    <p class="ml-6">{r.comment}</p>
                </div>

                <hr class="border-t border-gray-300 my-4" />
            {/if}
        {/each}

        <Pagination
                count={lastPage}
                pageSize={1}
                {page}
                onPageChange={(event) => {
            page = event.page;
            loadReviews(page);
        }}
                class="mt-6 flex justify-center"
        >
            <Pagination.PrevTrigger>
                <Icon name="leftArrow"/>
            </Pagination.PrevTrigger>

            <div class="flex space-x-2">
                {#each pages as p}
                    {#if p === 'ellipsis'}
                        <span class="w-8 flex items-center justify-center">…</span>
                    {:else}
                        <button
                                class={`w-8 h-8 rounded flex items-center justify-center cursor-pointer
                            ${p === page ? 'bg-primary-500 text-white font-bold' : 'bg-gray-200'}`}
                                on:click={() => loadReviews(p)}
                        >{p}</button>
                    {/if}
                {/each}
            </div>

            <Pagination.NextTrigger>
                <Icon name="rightArrow"/>
            </Pagination.NextTrigger>
        </Pagination>

    </div>
{/if}
