<script lang="ts">
  // Ejemplo de lo que probablemente tengas, pero vos lo armás:
   import { onMount } from 'svelte';
   import { t } from '$lib/i18n/i18n';
   import FormError from '$lib/components/global/forms/FormError.svelte';
   import BigButton from '$lib/components/global/BigButton.svelte';
	import type { Service } from '$models/Service';
	import { getServiceById } from '$services/serviceService';
    import { base } from '$app/paths';
  
    import { page } from '$app/stores';
    import { InvalidUrlParamError } from '$models/exceptions/InvalidUrlParamError';
	import { AppointmentForm, type AppointmentFormErrors } from '$models/forms/AppointmentCreationForm';

    let serviceId = Number( $page.params.id)
   let appointmentForm: AppointmentForm = new AppointmentForm();
   let formErrors: AppointmentFormErrors = {};
    let service :Service;
   
   onMount(async ()  => { 
    if ( serviceId==null )
        throw new InvalidUrlParamError("id must be a number")
    
    service = await getServiceById( serviceId )
   });
   function handleSubmit() {  }
</script>

{#if service}
  <div class="flex justify-center px-4 py-8">
    <form
      class="w-full max-w-md rounded-2xl shadow p-6 space-y-6"
      on:submit|preventDefault={handleSubmit}
    >
      <!-- Título -->
      <h2 class="text-lg font-semibold">
        {$t('appointment.create')} {service.serviceName}
      </h2>

      <!-- Servicio a domicilio / ubicación -->
      {#if service.homeService}
        <!-- Select de barrio/zona -->
        <div class="space-y-1">
          <label for="neighbourhood" class="block text-sm font-medium">
            {$t('appointment.home-service')}
          </label>
          <p class="text-xs opacity-80">
            {$t('input.appointment-neighbourhood')}
          </p>
          <select
            id="neighbourhood"
            name="neighbourhood"
            class="w-full border rounded-lg px-3 py-2 text-sm"
            bind:value={appointmentForm.neighbourhood}
          >
            {#each service.neighbourhoods ?? [] as neighbour}
              <option value={neighbour}>{neighbour}</option>
            {/each}
          </select>
        </div>

        <!-- Dirección del cliente -->
        <div class="space-y-1">
          <label for="location" class="block text-sm font-medium">
            {$t('address')}
          </label>
          <input
            id="location"
            name="location"
            type="text"
            class="w-full border rounded-lg px-3 py-2 text-sm"
            placeholder={$t('input.address')}
            bind:value={appointmentForm.location}
          />
          {#if formErrors.location}
            <FormError errorMessage={formErrors.location} />
          {/if}
        </div>
      {:else}
        <!-- Servicio en local / ubicación fija -->
        <div class="space-y-1">
          <p class="text-sm font-medium">
            {$t('appointment.location')} {service.address}
          </p>
        </div>

        <!-- Campo location “informativo” (como en el JSP) -->
        <div class="space-y-1">
          <label for="location" class="block text-sm font-medium">
            {$t('address')}
          </label>
          <input
            id="location"
            name="location"
            type="text"
            class="w-full rounded-lg px-3 py-2 text-sm border border-dashed"
            placeholder={$t('input.address')}
            bind:value={appointmentForm.location}
          />
          {#if formErrors.location}
            <FormError errorMessage={formErrors.location} />
          {/if}
        </div>
      {/if}

      <!-- Fecha y hora -->
      <div class="space-y-1">
        <label for="date" class="block text-sm font-medium">
          {$t('appointment.date')}
        </label>
        <input
          id="date"
          name="date"
          type="datetime-local"
          class="w-full border rounded-lg px-3 py-2 text-sm"
          bind:value={appointmentForm.date}
        />
        {#if formErrors.date}
          <FormError errorMessage={formErrors.date} />
        {/if}
      </div>

      <!-- Descripción -->
      <div class="space-y-1">
        <label for="description" class="block text-sm font-medium">
          {$t('appointment.description')}
        </label>
        <textarea
          id="description"
          name="description"
          maxlength="255"
          class="w-full border rounded-lg px-3 py-2 text-sm min-h-[6rem]"
          placeholder={$t('input.appointment.description')}
          bind:value={appointmentForm.description}
        />
        {#if formErrors.description}
          <FormError errorMessage={formErrors.description} />
        {/if}
      </div>

      <!-- Duración -->
      {#if service.duration > 0}
        <p class="text-sm">
          {$t('appointment.duration-general')}
          <span class="font-medium">
            {$t('appointment.duration-defined', [ service.duration ])}
          </span>
        </p>
      {:else}
        <p class="text-sm">
          {$t('appointment.duration-general')}
          <span class="font-medium">
            {$t('appointment.duration-not-defined')}
          </span>
        </p>
      {/if}

      <!-- Botones -->
      <div class="flex justify-between items-center pt-4 gap-4">
        <a href="{base}/services/{serviceId}" class="inline-flex">
          <button
            type="button"
            class="px-4 py-2 rounded-full border text-sm font-medium"
          >
            {$t('cancel')}
          </button>
        </a>

        <BigButton title={$t('appointment.submit')} iconName="" />
      </div>
    </form>
  </div>
{/if}
