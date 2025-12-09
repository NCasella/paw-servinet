<script lang="ts">
  import type { Appointment } from '$models/Appointment';
  import { AppointmentStatus } from '$models/enums/AppointmentStatus';
  import type { User } from '$models/User';
  import type { Service } from '$models/Service';
  import {t} from '$i18'

  export let appointment: Appointment;
  export let user: User;
  export let service: Service;

  // Datos que no vienen en las clases pero quizá sí de la API o de otro lado:
  export let businessName: string = '';      // nombre del negocio del servicio
  export let serviceUrl: string | null = null; // link a la página del servicio
  export let isPreviousAppointment = false;  // equivalente a appointment.previous en el JSP

  // Derivados
  $: confirmed = appointment.status === AppointmentStatus.CONFIRMED;

  // Ubicación: si es a domicilio uso la del turno, si no la del servicio
  $: finalLocation = service.homeService
    ? (appointment.address || service.address)
    : service.address;

  // Precio a determinar (ajustá según cómo guardes el tipo de pricing)
  $: isPriceTbd =
    service.pricingType === 'TBD' /* || service.pricingType === PricingTypes.TBD */;

  // Formato de fecha (rellenalo bien vos)
  $: startDateFormatted = formatAppointmentDate(appointment.startDate);

  function formatAppointmentDate(dateIso: string): string {
    // TODO: formatear como quieras
    return dateIso;
  }

  // Popup cancelar turno
  let showCancelPopup = false;

  function handleBackClick() {
    // TODO: navegar a la página del servicio (p.ej. goto(serviceUrl ?? `/services/${service.serviceId}`))
  }

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

<div class="max-w-3xl mx-auto px-4 py-8 space-y-6">
  <!-- Back -->
  <button
    type="button"
    class="inline-flex items-center gap-2 text-sm mb-2"
    on:click={handleBackClick}
  >
    <span class="material-icons text-base">arrow_back</span>
    <span>{$t('back.service')}</span>
  </button>

  <!-- Estado general del turno -->
  <div class="rounded-2xl shadow p-5">
    <div class="flex flex-col items-center gap-3 text-center">
      {#if confirmed}
        <span class="material-icons text-3xl text-green-600">check</span>
        <h2 class="text-lg font-semibold">
          {$t('appointment.ready')}
          <!-- si usás params: {$t('appointment.ready', { name: user.fullName })} -->
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
        <span class="material-icons text-base">storefront</span>
        {#if serviceUrl}
          <a href={serviceUrl} class="underline decoration-dotted">
            {service.serviceName}
            {#if businessName} – {businessName}{/if}
          </a>
        {:else}
          <span>
            {service.serviceName}
            {#if businessName} – {businessName}{/if}
          </span>
        {/if}
      </p>

      {#if service.description}
        <p class="text-sm opacity-80">
          {service.description}
        </p>
      {/if}

      <p class="text-sm mt-1">
        {#if isPriceTbd}
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
        <span class="material-icons text-base">person</span>
        <span>{user.fullName}</span>
      </p>

      <!-- Fecha -->
      <p class="flex items-center gap-2 text-sm">
        <span class="material-icons text-base">calendar_today</span>
        <span>{startDateFormatted}</span>
      </p>

      <!-- Ubicación -->
      <p class="flex items-center gap-2 text-sm">
        <span class="material-icons text-base">location_on</span>
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
  {#if !isPreviousAppointment}
    <div class="flex justify-center">
      <button
        type="button"
        class="btn variant-outline"
        on:click={openCancelPopup}
      >
        {$t('appointment.cancel')}
      </button>
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
