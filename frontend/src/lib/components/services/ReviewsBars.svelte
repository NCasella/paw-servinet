<script lang="ts">
    import Icon from "$icons";
    import type { Review } from "$models/Review";
    import {t} from '$i18'

    export let reviews: Review[] = [];

    $: allRatingCount = reviews.length;

    $: rating = allRatingCount === 0
        ? 0
        : Number(
            (reviews.reduce((acc, r) => acc + r.rating, 0) / allRatingCount).toFixed(1)
        );


    $: ratingCountList = [5, 4, 3, 2, 1].map(stars => ({
        key: stars,
        value: reviews.filter(r => r.rating === stars).length
    }));
</script>

<div class="flex mb-6">
    <div class="mr-10">
        {#each ratingCountList as count}
            <div class="flex items-center mb-2">
                <span class="w-6 font-medium">{count.key}</span>

                <progress
                    max="100"
                    value={allRatingCount === 0 ? 0 : 100 * count.value / allRatingCount}
                    class="w-80 h-3 rounded-2xl overflow-hidden
                        [&::-webkit-progress-bar]:rounded-2xl
                        [&::-webkit-progress-bar]:bg-gray-200
                        [&::-webkit-progress-value]:rounded-2xl
                        [&::-webkit-progress-value]:bg-yellow-400
                        [&::-moz-progress-bar]:rounded-2xl
                        [&::-moz-progress-bar]:bg-yellow-400"
                />
            </div>
        {/each}
    </div>

    <div class="flex flex-col items-center justify-center align-middle ml-10">
        <h1 class="text-5xl font-bold">{rating}</h1>

        <div class="flex mt-1">
            {#each Array(Math.floor(rating)) as _}
                <div class="text-yellow-400">
                    <Icon name="star"/>
                </div>
            {/each}

            {#each Array(5 - Math.floor(rating)) as _}
                <div class="text-surface-400">
                    <Icon name="star"/>
                </div>
            {/each}
        </div>

        <p class="text-surface-500 mt-1">
            {allRatingCount}{' '}{$t("reviews.opinions-amount")}
        </p>
    </div>
</div>
