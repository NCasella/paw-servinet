
<script lang="ts">
  import { onMount } from "svelte";
  import { goto } from "$app/navigation";
  import { base } from "$app/paths";
  import { t } from "$i18";

  import FormError from "$lib/components/global/forms/FormError.svelte";
  import BigButton from "$lib/components/global/BigButton.svelte";

  import { ServiceForm, type ServiceFormErrors } from "$models/forms/ServiceCreationForm";
  import type { Service } from "$models/Service";

  import { PricingTypes, PricingTypesInfo, PricingTypesList } from "$models/enums/PricingType";
  import { DurationTypesList } from "$models/enums/DurationType";

  import { updateService } from "$services/serviceService";
import type { PageData } from "./$types";
	import Icon from "$icons";

  export let data :PageData
    let { service } = data

  let serviceForm: ServiceForm;
  let formErrors: ServiceFormErrors = {};
  let saving = false;

  const durationTypes = DurationTypesList;

  onMount(() => {
    // precarga solo lo editable
    serviceForm = new ServiceForm({
      description: service.description ?? "",
      pricingType: service.pricingType ?? "",
      minimalDuration: service.duration ?? 0,
      additionalCosts: (service as any).additionalCosts ?? false
    });

    // si tu Service trae price (si no, queda null)
    serviceForm.price = (service as any).price ?? null;
  });

  function resetForm() {
    formErrors = {};
    serviceForm.description = service.description ?? "";
    serviceForm.pricingType = service.pricingType ?? "";
    serviceForm.minimalDuration = service.duration ?? 0;
    serviceForm.additionalCosts = (service as any).additionalCosts ?? false;
    serviceForm.price = (service as any).price ?? null;
  }

  async function handleSubmit() {
    formErrors = serviceForm.validateServiceUpdateForm();
    if (Object.keys(formErrors).length > 0) return;

    saving = true;
    try {
      console.log(serviceForm.additionalCosts)
      await updateService(service.serviceId, {
        description: serviceForm.description,
        minimalDuration: serviceForm.minimalDuration,
        pricingType: serviceForm.pricingType,
        price: serviceForm.pricingType === PricingTypes.TBD ? null : serviceForm.price,
        additionalCosts: serviceForm.additionalCosts
      });

      await goto(`${base}/services/${service.serviceId}`);
    } finally {
      saving = false;
    }
  }
</script>

{#if serviceForm}
  <div class="flex justify-center px-4 py-8">
    <form class="w-full max-w-xl rounded-2xl shadow p-6 space-y-6 bg-white"
          on:submit|preventDefault={handleSubmit}>

      <!-- header -->
      <div class="flex items-start justify-between gap-4">
        <div class="min-w-0">
          <h2 class="text-xl font-semibold truncate">{service.serviceName}</h2>
          <p class="text-sm opacity-70">{$t("service.edit")}</p>
        </div>

        <button type="button" class="btn rounded bg-surface-100 " on:click={() => goto(`${base}/services/${service.serviceId}`)}>
          x
        </button>
      </div>

      <!-- descripción -->
      <div class="space-y-1">
        <label for="description" class="block text-sm font-medium">
          {$t("service.description")}
        </label>
        <textarea id="description" maxlength="255"
          class="w-full border rounded-lg px-3 py-2 text-sm min-h-[7rem]"
          bind:value={serviceForm.description}
        />
        {#if formErrors.description}
          <FormError errorMessage={formErrors.description} />
        {/if}
      </div>

      <!-- precio -->
      <div class="space-y-2">
        <p class="text-sm font-medium">{$t("service.price")}</p>

        <label class="inline-flex items-center gap-2 text-sm">
          <input type="checkbox" class="checkbox" bind:checked={serviceForm.additionalCosts} />
          <span>{$t("service.additionalCharges")}</span>
        </label>

        <div class="grid gap-3 sm:grid-cols-2 items-start">
          <div class="space-y-1">
            <label for="pricingType" class="block text-sm font-medium">
              {$t('service.price')}
            </label>
            <select id="pricingType" class="w-full border rounded-lg px-3 py-2 text-sm"
                    bind:value={serviceForm.pricingType}>
              {#each PricingTypesList as type}
                <option value={type}>{$t(PricingTypesInfo[type].codeMsg)}</option>
              {/each}
            </select>
            {#if formErrors.pricingType}
              <FormError errorMessage={formErrors.pricingType} />
            {/if}
          </div>

          {#if serviceForm.pricingType !== PricingTypes.TBD}
            <div class="space-y-1">
              <label for="price" class="block text-sm font-medium">
                {$t("input.service.price")}
              </label>
              <!-- si querés evitar getter/setter, cambiá a bind:value={serviceForm.price} -->
              <input id="price" type="text"
                class="w-full border rounded-lg px-3 py-2 text-sm"
                bind:value={serviceForm.priceValue}
              />
              {#if formErrors.price}
                <FormError errorMessage={formErrors.price} />
              {/if}
            </div>
          {/if}
        </div>
      </div>

      <!-- duración -->
      <div class="space-y-2">
        <p class="text-sm font-medium">{$t("service.duration")}</p>

        <div class="flex flex-wrap gap-2">
          {#each durationTypes as d}
            <button type="button"
              on:click={() => (serviceForm.minimalDuration = d.value)}
              class="px-3 py-1 rounded-full border text-xs transition"
              class:bg-primary-500={serviceForm.minimalDuration === d.value}
              class:text-white={serviceForm.minimalDuration === d.value}
              class:border-primary-600={serviceForm.minimalDuration === d.value}
            >
              {$t(d.codeMsg)}
            </button>
          {/each}
        </div>

        {#if formErrors.minimalDuration}
          <FormError errorMessage={formErrors.minimalDuration} />
        {/if}
      </div>

      <!-- acciones -->
      <div class="flex justify-end gap-2 pt-2">
        <BigButton title={$t('business.save-changes')} iconName="" />
      </div>
    </form>
  </div>
{/if}
