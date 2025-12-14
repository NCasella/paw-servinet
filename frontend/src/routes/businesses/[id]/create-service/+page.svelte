<script lang="ts">
	import { ServiceForm, type ServiceFormErrors } from "$models/forms/ServiceCreationForm";
	import { onMount } from "svelte";
    import { t } from '$i18'
	import FormError from "$lib/components/global/forms/FormError.svelte";
	import BigButton from "$lib/components/global/BigButton.svelte";
	import { PricingTypes, PricingTypesInfo, PricingTypesList } from "$models/enums/PricingType";
	import { DurationTypes, DurationTypesList } from "$models/enums/DurationType";
	import { Categories, CategoriesInfo, CategoriesList, CategoriesTypeList } from "$models/enums/CategoryType";
	import { NeighbourhoodsList, NeighbourhoodsMap } from "$models/enums/Neighbourhoods";
	import { goto } from "$app/navigation";
	import { base } from "$app/paths";
	import { createService } from "$services/serviceService";
	import { Business } from "$models/Business";
	import { getParamIdFromUrl } from "$lib/navigation/pageInfo";

    let serviceForm: ServiceForm;
    let formErrors: ServiceFormErrors = {};
 
    const neighbourhoods = NeighbourhoodsList
    const durationTypes = DurationTypesList
    let image: File | null = null;
    
    onMount(async () => {
       
      let businessId = getParamIdFromUrl()
      serviceForm = new ServiceForm({businessId:businessId});
    })

    async function handleSubmit() {
      formErrors = serviceForm.validateServiceForm();
      if (Object.keys(formErrors).length > 0) return

      /*
      if (Object.keys(formErrors).length > 0) {
          return;
      }*/
      const id = await createService(serviceForm, image);
      goto(`${base}/services/${id}`);
    }


    function handleImageChange(e: Event) {
      const target = e.target as HTMLInputElement;
      const files = target.files;
      image = files && files.length > 0 ? files[0] : null;
    }
   
    function toggleNeighbourhood(n: string) {
    if (serviceForm.neighbourhoods.includes(n)) {
      serviceForm.neighbourhoods = serviceForm.neighbourhoods.filter(x => x !== n);
    } else {
      serviceForm.neighbourhoods = [...serviceForm.neighbourhoods, n];
    }
  }
</script>

{#if serviceForm}
  <div class="flex justify-center px-4 py-8">
    <form
      class="w-full max-w-xl rounded-2xl shadow p-6 space-y-6"
      on:submit|preventDefault={handleSubmit}
      enctype="multipart/form-data"
    >
      <!-- Título -->
      <h2 class="text-xl font-semibold">
        {$t('service.create')}
      </h2>

      <!-- Nombre del servicio -->
      <div class="space-y-1">
        <label for="serviceName" class="block text-sm font-medium">
          {$t('service-name')}
        </label>
        <input
          id="serviceName"
          name="serviceName"
          type="text"
          class="w-full border rounded-lg px-3 py-2 text-sm"
          placeholder={$t('input.service.name')}
          bind:value={serviceForm.serviceName}
        />
        {#if formErrors.serviceName}
          <FormError errorMessage={formErrors.serviceName} />
        {/if}
      </div>

      <!-- Imagen -->
      <div class="space-y-1">
        <label for="image" class="block text-sm font-medium">
          {$t('input.service.image')}
        </label>
        <input
          id="image"
          name="image"
          type="file"
          accept=".png, .jpg, .jpeg"
          class="w-full border rounded-lg px-3 py-2 text-sm"
          on:change={handleImageChange}
        />
      </div>

      <!-- Descripción -->
      <div class="space-y-1">
        <label for="description" class="block text-sm font-medium">
          {$t('service.description')}
        </label>
        <textarea
          id="description"
          name="description"
          maxlength="255"
          class="w-full border rounded-lg px-3 py-2 text-sm min-h-[6rem]"
          placeholder={$t('input.service.description')}
          bind:value={serviceForm.description}
        />
        {#if formErrors.description}
          <FormError errorMessage={formErrors.description} />
        {/if}
      </div>

      <!-- Ubicación / servicio a domicilio -->
      <div class="space-y-2">
        <p class="text-sm font-medium">
          {$t('service.location')}
        </p>

        <label class="inline-flex items-center gap-2 text-sm">
          <input
            id="homeService"
            name="homeService"
            type="checkbox"
            class="checkbox"
            bind:checked={serviceForm.homeService}
          />
          <span>{$t('service.home-service')}</span>
        </label>

        <p class="text-xs opacity-80">
          {#if serviceForm.homeService}
            {$t('input.service.homeService')}
          {:else}
            {$t('input.service.noneHomeService')}
          {/if}
        </p>
        {#if serviceForm.homeService}
              <!-- barrios (multi-select) -->
              <div class="space-y-1">
                <label for="neighbourhoods" class="block text-sm font-medium">
                  {$t('input.service.select-neighbourhood')}
                </label>
                <div class="flex flex-wrap gap-2">
                  {#each neighbourhoods as n}
                    <button
                      type="button"
                      class={`px-3 py-1 rounded-full border text-sm 
                        ${serviceForm.neighbourhoods.includes(n)
                          ? 'bg-primary-100 border-primary-600 text-primary-800'
                          : 'bg-gray-100 border-gray-300 text-gray-600'
                        }`}
                      on:click={() => toggleNeighbourhood(n)}
                    >
                      {NeighbourhoodsMap[n] }
                    </button>
                  {/each}
                </div>

                {#if formErrors.neighbourhoods}
                  <FormError errorMessage={formErrors.neighbourhoods} />
                {/if}
              </div>
        {:else}
          <!-- Neighbourhoods / address -->
          <div class="grid gap-3 sm:grid-cols-2">
            <div class="space-y-1">
              <label for="neighbourhoods" class="block text-sm font-medium">
                {$t('input.service.select-neighbourhood')}
              </label>
              <select
                id="neighbourhoods"
                name="neighbourhoods"
                class="w-full border rounded-lg px-3 py-2 text-sm"
                bind:value={serviceForm.neighbourhoods}
              >
                {#each neighbourhoods as n}
                  <option value={[n]}>{n}</option>
                {/each}
              </select>
              {#if formErrors.neighbourhoods}
                <FormError errorMessage={formErrors.neighbourhoods} />
              {/if}
            </div>
          
          <!-- dirección libre -->
          <div class="space-y-1">
            <label for="address" class="block text-sm font-medium">
              {$t('address')}
            </label>
            <input
              id="address"
              name="address"
              type="text"
              class="w-full border rounded-lg px-3 py-2 text-sm"
              placeholder={$t('input.service.location')}
              bind:value={serviceForm.address}
            />
            {#if formErrors.address}
              <FormError errorMessage={formErrors.address} />
            {/if}
          </div>

        </div>
        {/if}
      </div>

      <!-- Precio -->
      <div class="space-y-2">
        <p class="text-sm font-medium">
          {$t('service.price')}
        </p>

        <label class="inline-flex items-center gap-2 text-sm">
          <input
            id="additionalCharges"
            name="additionalCharges"
            type="checkbox"
            class="checkbox"
            bind:checked={serviceForm.additionalCharges}
          />
          <span>{$t('service.additionalCharges')}</span>
        </label>

        <div class="grid gap-3 sm:grid-cols-2 items-start">
          <!-- Tipo de precio -->
          <div class="space-y-1">
            <label for="pricingType" class="block text-sm font-medium">
              {$t('service.price')}
            </label>
            <select
              id="pricingType"
              name="pricingType"
              class="w-full border rounded-lg px-3 py-2 text-sm"
              bind:value={serviceForm.pricingType}
            >
              {#each PricingTypesList as type}
                <option value={type}>
                  {$t(PricingTypesInfo[type].codeMsg)}
                </option>
              {/each}
            </select>
            {#if formErrors.pricingType}
              <FormError errorMessage={formErrors.pricingType} />
            {/if}
          </div>

          <!-- Monto -->
          {#if serviceForm.pricingType!=PricingTypes.TBD}
            <div class="space-y-1">
            <label for="price" class="block text-sm font-medium">
              {$t('input.service.price')}
            </label>
            <input
              id="price"
              name="price"
              type="text"
              class="w-full border rounded-lg px-3 py-2 text-sm"
              placeholder={$t('input.service.price')}
              bind:value={serviceForm.priceValue}
            />
            {#if formErrors.price}
              <FormError errorMessage={formErrors.price} />
            {/if}
          </div>
          {/if}
          
        </div>
      </div>

      <!-- Categoría -->
      <div class="space-y-1">
        <label for="category" class="block text-sm font-medium">
          {$t('service.category')}
        </label>
        <select
          id="category"
          name="category"
          class="w-full border rounded-lg px-3 py-2 text-sm"
          bind:value={serviceForm.category}
        >
          {#each CategoriesTypeList as c}
            <option value={c}>
                  {$t(CategoriesInfo[c].codeMsg)}
              </option>
          {/each}
        </select>
        {#if formErrors.category}
          <FormError errorMessage={formErrors.category} />
        {/if}
      </div>

      <!-- Duración mínima -->
      <div class="space-y-2">
        <p class="text-sm font-medium">
          {$t('service.duration')}
        </p>
        <p class="text-xs opacity-80">
          {$t('input.service.duration')}
        </p>

        <div class="flex flex-wrap gap-2">
  {#each durationTypes as d}
    <button
      type="button"
      on:click={() => (serviceForm.minimalDuration = d.value)}
      class="
        px-3 py-1 rounded-full border text-xs transition
      "
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

      <!-- Submit -->
      <div class="flex justify-center pt-2">
        <BigButton title={$t('input.service.publish')} iconName="" />
      </div>
    </form>
  </div>
{/if}
