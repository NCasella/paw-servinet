<script lang="ts">
    import Title from "$lib/components/global/Title.svelte";
    import BigButton from "$lib/components/global/BigButton.svelte";
    import {t} from "$lib/i18n/i18n"
    import { base } from "$app/paths";
    import {onMount} from "svelte";
    import {getCurrentUser} from "$services/userService";
    import {goto} from "$app/navigation";
    import {deleteService} from "$services/serviceService";
    import type {Business} from "$models/Business";
    import {getBusinessById} from "$services/businessService";
    import Icon from "$icons";
    import {PricingTypes, PricingTypesInfo} from "$models/enums/PricingType";
    import type {User} from "$models/User";
    import {Categories, CategoriesInfo} from "$models/enums/CategoryType";
    import BigButtonSecondary from "$lib/components/global/BigButtonSecondary.svelte";
    import BigButtonWarning from "$lib/components/global/BigButtonWarning.svelte";
    import { Dialog, Portal } from '@skeletonlabs/skeleton-svelte';
    import {get} from "svelte/store";
    import Spinner from "$lib/components/global/Spinner.svelte";
    import Questions from "$lib/components/services/Questions.svelte";
    import Reviews from "$lib/components/services/Reviews.svelte";

    let serviceId :number
    let loading = true
    let isOwner :boolean
    let isQuestions = true;
    
    let user :User
    let business :Business

    let pricingEnum: PricingTypes;
    let categoryEnum: Categories;

    import type { PageData } from './$types';
    export let data :PageData
    const { service } = data
    serviceId = service.serviceId
    onMount(async () => {

        business = await getBusinessById(service.businessId);
        try {
            user = await getCurrentUser();
            isOwner = user? user.userId === business.userId : false;
        } catch (e) {
            isOwner = false;
        }

        pricingEnum = PricingTypes[service.pricingType as keyof typeof PricingTypes];
        categoryEnum = Categories[service.category as keyof typeof Categories];

        loading = false
    })

    async function handleServiceDelete() {
        try {
            await deleteService(serviceId);
            goto(`${base}/businesses/${business.businessId}`);
        } catch (error) {
            alert(get(t)("service.delete-error"));
            throw error
        }
    }

    function showQuestions() {
        isQuestions = true;
    }

    function showReviews() {
        isQuestions = false;
    }
</script>

{#if loading}
    <Spinner/>
{:else}
    <header>
        <Title text={service.serviceName}/>
    </header>
    <div class="mb-20">
        <div class="flex justify-end items-center mt-4">
            {#if isOwner}
                <div class="flex gap-2">
                     <div class="flex gap-2">
                    <a href="{base}/services/{serviceId}/edit">
                        <BigButtonSecondary title={$t("service.edit")} iconName=""/>
                    </a>
                    </div>
                    <Dialog role="alertdialog">
                        <Dialog.Trigger>
                            <BigButtonWarning title={$t("service.delete") }/>
                        </Dialog.Trigger>
                        <Portal>
                            <Dialog.Backdrop class="fixed inset-0 z-50" />
                            <Dialog.Positioner class="fixed inset-0 z-50 flex justify-center items-center">
                                <Dialog.Content class="card preset-filled bg-white w-md space-y-2 shadow-xl rounded-2xl p-10">
                                    <Dialog.Title class="text-2xl text-black font-bold">{$t("popup.service.title")}</Dialog.Title>
                                    <Dialog.Description class="text-black">{$t("popup.service.message")}</Dialog.Description>
                                    <Dialog.CloseTrigger class="flex gap-5">
                                        <BigButtonSecondary title={$t("service.cancel")} iconName=""/>
                                        <div on:click={handleServiceDelete}>
                                            <BigButtonWarning title={$t("service.delete")} iconName="" onclick={()=>{}}/>
                                        </div>
                                    </Dialog.CloseTrigger>
                                </Dialog.Content>
                            </Dialog.Positioner>
                        </Portal>
                    </Dialog>
                </div>
            {/if}
        </div>

        <div class="flex gap-8 mt-6">
            <div class="w-120 rounded-lg overflow-hidden">
                <img src={service.getServiceImageUrl()} alt="Service image" />
            </div>

            <div class="flex-1">
                <h3 class="text-xl font-semibold mb-2">{$t("service.info")}</h3>

                <div class="flex justify-between items-center mb-2">
                    <p class="text-gray-600">{$t(CategoriesInfo[categoryEnum].codeMsg)}</p>
                    <div class="flex items-center gap-1">
                        <p>{service.rating > 0 ? service.rating : $t("service.unrated")}</p>
                        <div class="text-yellow-400">
                            <Icon name="star"/>
                        </div>
                    </div>
                </div>

                <p class="flex items-center gap-1 mb-1">
                    <Icon name="location"/>
                    {#each service.neighbourhoods as neighbour}
                        {neighbour}{#if !neighbour.endsWith(service.neighbourhoods[service.neighbourhoods.length-1])}, {/if}
                    {/each}
                </p>

                <p class="flex items-center gap-1 mb-1">
                    <Icon name="address"/>
                    {#if service.homeService}
                        Atención a domicilio
                    {:else}
                        {service.address}
                    {/if}
                </p>

                <p class="flex items-center gap-1 mb-1">
                    <Icon name="timer"/>
                    {service.duration} min
                </p>

                <p class="flex items-center gap-1 mb-1">
                    <Icon name="money"/>
                    {#if pricingEnum === PricingTypes.TBD}
                        <span class="text-gray-500 italic">{$t(PricingTypesInfo[pricingEnum].codeMsg)}</span>
                    {:else}
                        {service.price} <span class="text-gray-500 italic">{$t(PricingTypesInfo[pricingEnum].codeMsg)}</span>
                    {/if}
                </p>

                {#if service.additionalCosts}
                    <p class="flex items-center gap-1 text-error-400 mb-2">
                        <Icon name="warning"/>
                        {$t("service.additional-costs")}
                    </p>
                {/if}

                <a href="{base}/businesses/{business.businessId}" class="flex items-center text-primary-500 font-bold hover:underline">
                    <Icon name="business"/>
                    {business.businessName}
                </a>

                {#if !isOwner}
                    <div class="flex mt-3 justify-center">
                        <a href="{base}/services/{serviceId}/create-appointment">
                            <BigButton title={$t("service.new-appointment")} iconName=""/>
                        </a>
                    </div>
                {/if}
            </div>
        </div>

        <div class="mt-6 p-4 bg-gray-100 rounded">
            <p>{service.description}</p>
        </div>

        <div class="flex justify-center m-4">
            <button
                    class="px-4 py-2 bg-primary-200 rounded-l-3xl font-bold text-white hover:bg-primary-500"
                    class:bg-primary-500={isQuestions}
                    on:click={showQuestions}
            >
                Questions
            </button>
            <button
                    class="px-4 py-2 bg-primary-200 rounded-r-3xl font-bold text-white hover:bg-primary-500"
                    class:bg-primary-500={!isQuestions}
                    on:click={showReviews}
            >
                Reviews
            </button>
        </div>

        <div class={isQuestions ? "" : "opacity-0 h-0 overflow-hidden pointer-events-none"}>
            <Questions serviceId={serviceId} isOwner={isOwner} user={user}/>
        </div>

        <div class={!isQuestions ? "" : "opacity-0 h-0 overflow-hidden pointer-events-none"}>
            <Reviews serviceId={serviceId} isOwner={isOwner} user={user}/>
        </div>
    </div>
{/if}