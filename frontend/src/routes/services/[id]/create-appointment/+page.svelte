<script lang="ts">
  // Ejemplo de lo que probablemente tengas, pero vos lo armás:
   import { onMount } from 'svelte';
   import { t } from '$lib/i18n/i18n';
   import FormError from '$lib/components/global/forms/FormError.svelte';
   import BigButton from '$lib/components/global/BigButton.svelte';
	import type { Service } from '$models/Service';
	import { getServiceById } from '$services/serviceService';
  import { base } from '$app/paths';
  import { page } from '$app/state';
  
  import { InvalidUrlParamError } from '$models/exceptions/InvalidUrlParamError';
	import { AppointmentForm, type AppointmentFormErrors } from '$models/forms/AppointmentCreationForm';
	import { createAppointment } from '$services/appointmentService';
	import { goto } from '$app/navigation';
	import { getParamIdFromUrl } from '$lib/navigation/pageInfo';
  import {getCurrentUser} from "$services/userService";
  import {User} from "$models/User";
	import { toNeighbourhoodEnum } from '$models/enums/Neighbourhoods';
	import type { PageData } from './$types';

  
  export let data:PageData
  let { user, service, business, serviceId} = data

  let appointmentForm: AppointmentForm  = new AppointmentForm({serviceId:serviceId?? "", address:service.address?? "" });
  let formErrors: AppointmentFormErrors = {};
  
   async function handleSubmit() { 
        formErrors  = appointmentForm.validateAppointmentForm()
        if (Object.keys(formErrors).length > 0) return

        const appointmentId = await createAppointment(appointmentForm)
        goto(`${base}/appointments/${appointmentId}`)
    }
</script>

<svelte:head>
    <title>{service ? $t('title.reserve-appointment') : $t('title.servinet')}</title>
</svelte:head>

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
          <label for="neighborhood" class="block text-sm font-medium">
            {$t('appointment.home-service')}
          </label>
          <p class="text-xs opacity-80">
            {$t('input.appointment-neighbourhood')}
          </p>
          <select
            id="neighborhood"
            name="neighborhood"
            class="w-full border rounded-lg px-3 py-2 text-sm"
            bind:value={appointmentForm.neighborhood}
          >
            {#each service.neighbourhoods ?? [] as neighbour}
              <option value={ toNeighbourhoodEnum(neighbour)}>{neighbour}</option>
            {/each}
          </select>
        </div>

        <!-- Dirección del cliente -->
        <div class="space-y-1">
          <label for="location" class="block text-sm font-medium">
            {$t('address')}
          </label>
          <input
            maxlength="255"
            id="location"
            name="location"
            type="text"
            class="w-full border rounded-lg px-3 py-2 text-sm"
            placeholder={$t('input.address')}
            bind:value={appointmentForm.address}
          />
          {#if formErrors.address}
            <FormError errorMessage={formErrors.address} />
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
          bind:value={appointmentForm.startDate}
        />
        {#if formErrors.startDate}
          <FormError errorMessage={formErrors.startDate} />
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
            on:click={() => history.back()}
            class="px-4 py-2 rounded-full border text-sm font-medium"
          >
            {$t('cancel')}
          </button>
        </a>

        <BigButton title={$t('appointment.submit')} iconName=""  />
      </div>
    </form>
  </div>
{/if}
