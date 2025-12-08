<script lang="ts">
    import Title from "$lib/components/global/Title.svelte";
    import BigButton from "$lib/components/global/BigButton.svelte";
    import {t} from "$lib/i18n/i18n"
    import { base } from "$app/paths";
    import type {User} from "$models/User";
    import {onMount} from "svelte";
    import {closeSession, getCurrentUser} from "$services/userService";
    import {goto} from "$app/navigation";
    import type {Service} from "$models/Service";
    import {getServiceById} from "$services/serviceService";
    import type {PagedResult} from "$models/PagedList";
    import type {Business} from "$models/Business";
    import {getUserBusinesses} from "$services/businessService";
    import Icon from "$icons";
    import {page} from "$app/stores";
    import {InvalidUrlParamError} from "$models/exceptions/InvalidUrlParamError";
    import {PricingTypes, PricingTypesInfo} from "$models/enums/PricingType";

    let serviceId :number
    let loading = true
    let service:Service
    let user:User

    let pricingEnum: PricingTypes;

    onMount(async () => {
        try {
            serviceId = Number( $page.params.id)
        } catch {throw new InvalidUrlParamError("id must be a number") }

        user = await getCurrentUser()

        service = await getServiceById(serviceId)
            .then(s => s)
            .finally(() => loading = false);

        pricingEnum = PricingTypes[service.pricingType as keyof typeof PricingTypes];
        console.log(pricingEnum);
        console.log(service.pricingType)
    })


    let isOwner = false;

    function showPopUp() {
        alert("¿Seguro que desea eliminar el servicio?");
    }

    let contratarUrl = `/reservar`;
</script>

{#if service}
    <header>
        <Title text={service.serviceName}/>
    </header>
    <div>
        <button on:click={() => history.back()} class="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400">⬅ Volver</button>

        <div class="flex justify-between items-center mt-4">
            {#if isOwner}
                <div class="flex gap-2">
                    <a href={`/editar-servicio/${service.serviceId}`}>
                        <button class="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600">Editar</button>
                    </a>
                    <button on:click={showPopUp} class="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600">Eliminar</button>
                </div>
            {/if}
        </div>

        <div class="flex gap-8 mt-6">
            <div class="w-72">
            </div>

            <div class="flex-1">
                <h3 class="text-xl font-semibold mb-2">Detalles</h3>

                <div class="flex justify-between items-center mb-2">
                    <p class="text-gray-600">{PricingTypesInfo[PricingTypes.TBD].codeMsg}</p>
                    <div class="flex items-center gap-1">
                        <p>{service.rating > 0 ? service.rating : "Sin calificar"}</p>
                        <span class="text-yellow-400 text-lg">⭐</span>
                    </div>
                </div>

                <p class="flex items-center gap-1 mb-1">📍
                    {#each service.neighbourhoods as neighbour}
                        {neighbour}{#if !neighbour.endsWith(service.neighbourhoods[service.neighbourhoods.length-1])}, {/if}
                    {/each}
                </p>

                <p class="flex items-center gap-1 mb-1">🏠
                    {#if service.homeService}
                        Atención a domicilio
                    {:else}
                        {service.address}
                    {/if}
                </p>

                <p class="flex items-center gap-1 mb-1">⏱ Tiempo: {service.duration}</p>

                <p class="flex items-center gap-1 mb-1">💲
                    {#if PricingTypes.TBD === PricingTypes.TBD}
                        <span class="text-gray-500 italic">{PricingTypesInfo[PricingTypes.TBD].codeMsg}</span>
                    {:else}
                        {service.price} <span class="text-gray-500 italic">{PricingTypesInfo[PricingTypes.TBD].codeMsg}</span>
                    {/if}
                </p>

                {#if service.additionalCosts}
                    <p class="flex items-center gap-1 text-red-600 mb-2">⚠ Costos adicionales</p>
                {/if}

                <a href={`/negocio/${service.businessId}`} class="text-blue-600 hover:underline">
                    🏪 NOMBRE DEL SERVICIO
                </a>

                {#if !isOwner}
                    <div class="mt-3">
                        <a href={contratarUrl}>
                            <button class="px-3 py-1 bg-green-500 text-white rounded hover:bg-green-600">Reservar turno</button>
                        </a>
                    </div>
                {/if}
            </div>
        </div>

        <div class="mt-6 p-4 bg-gray-100 rounded">
            <p>{service.description}</p>
        </div>
    </div>
{/if}