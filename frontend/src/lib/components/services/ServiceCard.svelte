<script lang="ts">
    import type {Service} from "$models/Service";
    import {t} from "$lib/i18n/i18n"
    import {PricingTypes, PricingTypesInfo} from "$models/enums/PricingType";
    import {Categories, CategoriesInfo} from "$models/enums/CategoryType";
    import {getImage} from "$services/imageService";
    import {onMount} from "svelte";
    import {base} from "$app/paths";
    import Icon from "$icons";

    export let service: Service;
    let imageUrl: string = "";

    onMount(async () => {
        imageUrl = await getImage(service.imageId)
    });

    let pricingEnum: PricingTypes = PricingTypes[service.pricingType as keyof typeof PricingTypes];
    let categoryEnum: Categories = Categories[service.category as keyof typeof Categories];

</script>

<a class="block bg-surface-100 rounded-xl p-3 shadow-md hover:bg-primary-100 transition cursor-pointer no-underline text-black mb-5"
   href="{base}/services/{service.serviceId}"
>
    <div class="flex gap-4">

        <div class="w-50 h-32 rounded-xl overflow-hidden flex-shrink-0">
            <div class="w-50 rounded-lg overflow-hidden">
                <img src={imageUrl} alt="Service image" />
            </div>
        </div>

        <div class="flex flex-col justify-between flex-grow">

            <div class="flex justify-between items-center w-full">
                <p class="text-sm text-gray-600">{$t(CategoriesInfo[categoryEnum].codeMsg)}</p>

                {#if pricingEnum === PricingTypes.TBD}
                    <p class="text-sm font-semibold">{$t(PricingTypesInfo[PricingTypes.TBD].codeMsg)}</p>
                {:else}
                    <p class="text-right font-semibold">${service.price}</p>
                {/if}
            </div>

            <div class="flex justify-between items-center w-full">
                <h3 class="text-lg font-semibold">{service.serviceName}</h3>

                <p class="flex items-center gap-1">
                    {service.rating > 0 ? service.rating : $t('service.unrated')}
                    <span class="text-yellow-400">
                        <Icon name="star"/>
                    </span>
                </p>
            </div>

            <p class="flex items-center gap-1 text-sm text-gray-700">
                <Icon name="location"/>

                {#if service.address}
                    {service.address},
                {/if}

                {#each service.neighbourhoods as n}
                    {n}{" "}
                {/each}
            </p>

        </div>
    </div>
</a>
