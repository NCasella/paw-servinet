<script lang="ts">
  import type { Appointment } from '$models/Appointment';
  import { AppointmentStatus } from '$models/enums/AppointmentStatus';
  import type { User } from '$models/User';
  import type { Service } from '$models/Service';
  import {t} from '$i18'
	import { onMount } from 'svelte';
	import { getCurrentUser } from '$services/userService';
	import { getParamIdFromUrl } from '$lib/navigation/pageInfo';
	import type { Business } from '$models/Business';
	import { getServiceById } from '$services/serviceService';
	import { getBusinessById } from '$services/businessService';
	import { base } from '$app/paths';
	import Spinner from '../global/Spinner.svelte';
	import { getAppointmentById } from '$services/appointmentService';
	import Icon from '$icons';
	import BigButtonWarning from '$lib/components/global/BigButtonWarning.svelte';

  let user :User, appointment :Appointment, service :Service, business :Business, status
  let finalLocation :string, loading = true
  onMount( async () => {
    user = await getCurrentUser()
    const appId = getParamIdFromUrl()
    appointment = await getAppointmentById(appId)
    service = await getServiceById(appointment.serviceId)
    business = await getBusinessById(service.businessId)
    finalLocation = service.homeService
    ? appointment.address 
    : $t("service.location");

    loading = false
  })
 
  // Ubicación: si es a domicilio uso la del turno, si no la del servicio
   

  // Precio a determinar (ajustá según cómo guardes el tipo de pricing)



  // Popup cancelar turno
  let showCancelPopup = false;

  function openCancelPopup() {
    showCancelPopup = true;
  }

  function closeCancelPopup() {
    showCancelPopup = false;
  }

  async function confirmCancelAppointment() {
    // TODO: llamar a la API para cancelar el turno
    // luego cerrar popup / redirigir
  }

</script>

{#if loading }
<Spinner/>
{:else if appointment}
<div class="max-w-3xl mx-auto px-4 py-8 space-y-6">
  
  <!-- Estado general del turno -->
  <div class="rounded-2xl shadow p-5">
    <div class="flex flex-col items-center gap-3 text-center">
      {#if appointment.isConfirmed()}
        <Icon name="accept"/>
        <h2 class="text-lg font-semibold">
          {$t('appointment.ready', [user.fullName])}
          
        </h2>
        <p class="text-sm opacity-80">
          {$t('appointment.confirmed')}
        </p>
      {:else}
        <span class="material-icons text-3xl text-yellow-500">schedule</span>
        <h2 class="text-lg font-semibold">
          {$t('appointment.waiting-confirmation')}
        </h2>
        <div class="text-sm opacity-80 space-y-1">
          <p>{$t('appointment.successfully-requested')}</p>
          <p>{$t('appointment.mail-send')}</p>
        </div>
      {/if}
    </div>
  </div>

  <!-- Detalle servicio + turno -->
  <div class="rounded-2xl shadow p-5 space-y-4">
    <!-- Info servicio -->
    <div class="space-y-2">
      <h3 class="text-base font-semibold">
        {$t('service.info')}
      </h3>

      <p class="flex items-center gap-2 text-sm">
        <Icon name="business"/>
        
          <a href='{base}/services/{service.serviceId}' class="underline decoration-dotted">
            {service.serviceName}
             - {business.businessName}
          </a>
        
      </p>

      {#if service.description}
        <p class="text-sm opacity-80">
          {service.description}
        </p>
      {/if}
      <p class="text-sm mt-1">
        {#if service.hasPriceTBD()}
          <span class="font-semibold">$ </span>
          <span class="italic">{$t('pricing.tbd')}</span>
        {:else}
          <span class="font-semibold">
            $ {service.price}
          </span>
        {/if}
      </p>
    </div>

    <!-- Info turno -->
    <div class="border-t pt-4 space-y-2">
      <h3 class="text-base font-semibold">
        {$t('appointment.info')}
      </h3>

      <!-- Persona -->
      <p class="flex items-center gap-2 text-sm">
        <Icon name="person"/>
        <span>{user.fullName}</span>
      </p>

      <!-- Fecha -->
      <p class="flex items-center gap-2 text-sm">
        <Icon name="calendar"/>
        <span>{appointment.formatedDate()}</span>
      </p>

      <!-- Ubicación -->
      <p class="flex items-center gap-2 text-sm">
        <Icon name="location"/>
        {#if !finalLocation}
          <span class="opacity-80">
            {$t('appointment.no-location')}
          </span>
        {:else}
          <span>{finalLocation}</span>
        {/if}
      </p>

      <!-- Número de seguimiento -->
      <p class="text-sm">
        <span class="font-semibold">
          {$t('appointment.tracking-number')}{" "}
        </span>
        <span>{appointment.appointmentId}</span>
      </p>

      <!-- Descripción del usuario -->
      {#if appointment.description}
        <p class="text-sm">
          <span class="font-semibold">
            {$t('appointment.user-description')}{" "}
          </span>
          <span>{appointment.description}</span>
        </p>
      {/if}
    </div>
  </div>

  <!-- Botón cancelar (si no es turno pasado) -->
  {#if !appointment.hasFinished()}
    <div class="flex justify-center">

      <BigButtonWarning onclick={openCancelPopup} title={$t("appointment.cancel")} iconName=""/>
    </div>
  {/if}

  <!-- Popup cancelar turno -->
  {#if showCancelPopup}
    <div class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white rounded-2xl shadow-lg max-w-sm w-full p-6 space-y-4">
        <h3 class="text-lg font-semibold">
          {$t('popup.appointment.title')}
        </h3>
        <p class="text-sm opacity-80">
          {$t('popup.appointment.message')}
        </p>

        <div class="flex justify-end gap-3 pt-2">
          <button
            type="button"
            class="btn variant-ghost"
            on:click={closeCancelPopup}
          >
            {$t('cancel')}
          </button>
          <button
            type="button"
            class="btn variant-destructive"
            on:click={confirmCancelAppointment}
          >
            {$t('popup.appointment.cancel')}
          </button>
        </div>
      </div>
    </div>
  {/if}
</div>
{/if}