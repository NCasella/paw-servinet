<script lang="ts">
  import type { Appointment } from '$models/Appointment';
  import { AppointmentStatus } from '$models/enums/AppointmentStatus';
  import type { User } from '$models/User';
  import type { Service } from '$models/Service';
  import {t} from '$i18'
	import Icon from '$icons';


  // Props principales
  export let appointment: Appointment;
  export let user: User;
  export let service: Service;
  export let status: AppointmentStatus;

  // ¿Es un turno del historial?
  export let history = status == AppointmentStatus.FINISHED;
  // ¿La vista es del usuario (cliente) o del provider?
  export let isUser = false;

  // Labels ya formateadas (vos te encargás de armarlas en el padre)
  export let dayLabel = appointment.formatedDate();           // startDateString
  export let dayWithYearLabel = '';   // startDateWithYearString
  export let timeStartLabel = appointment.formatedDate().getHours();     // startDateTimeString
  

  // Descripción larga para “leer más” (si querés separar de appointment.description)
  export let longDescription: string | null = null;

  // URLs que usabas en los <a> del JSP
  export let serviceUrl = '';          // /servicio/{serviceid}
  export let renewUrl = '';            // /contratar-servicio/{serviceid}
  export let userAppointmentUrl = '';  // /turno/{serviceid}/{appointmentId}

  // Callbacks que implementás en el padre
  export let onAccept: (appointment: Appointment) => void;
  export let onRequestCancel: (appointment: Appointment) => void;     // abrir popup cancel
  export let onReadMore: (appointment: Appointment) => void;          // abrir popup desc
  export let onOpenInfo: (appointment: Appointment) => void;          // ir a detalle

  // Estado interno: accordion
  let expanded = false;

  // Derivados
  $: confirmed = status === AppointmentStatus.CONFIRMED;
  $: dayText = history ? (dayWithYearLabel || dayLabel) : dayLabel;

  // Home service vs en el local
  $: locationText = service.homeService
    ? (appointment.address || service.address)
    : service.address;

  function handleAccept() {
    onAccept?.(appointment);
  }

  function handleCancelClick() {
    onRequestCancel?.(appointment);
  }

  function handleReadMore() {
    onReadMore?.(appointment);
  }

  function handleInfo() {
    onOpenInfo?.(appointment);
  }

  function toggleAccordion() {
    expanded = !expanded;
  }
</script>

<div class="w-full">
  <div class="flex flex-col gap-2 rounded-2xl shadow px-4 py-3 bg-white">
    <!-- fila principal -->
    <div class="grid grid-cols-[auto,auto,1fr,auto,auto] items-center gap-3">
      <!-- Día -->
      <span class="text-sm font-medium">
        {dayText}
      </span>

      <!-- Horario -->
      <span class="flex items-center gap-1 text-sm">
        <Icon name="schedule"/>
        {appointment.formatedTime()}

      </span>

      <!-- Nombre servicio -->
      <a
        class="text-sm hover:underline truncate"
        href={serviceUrl}
      >
        {service.serviceName}
      </a>

      <!-- ID turno -->
      <span class="text-xs opacity-70">
        #{appointment.appointmentId}
      </span>

      <!-- Botones decisión -->
      <div class="flex items-center gap-1 justify-end">
        {#if history}
          <!-- Renovar -->
          <button
            type="button"
            class="inline-flex items-center justify-center rounded-full border px-2 py-1 text-xs"
            on:click={() => {/* p.ej. navigate(renewUrl) */}}
            title="Renovar"
          >
            <span class="material-icons text-base">event_repeat</span>
          </button>
        {:else if confirmed || isUser}
          <!-- Solo cancelar -->
          <button
            type="button"
            class="inline-flex items-center justify-center rounded-full border px-2 py-1 text-xs"
            on:click={handleCancelClick}
          >
            <Icon name="cancel"/>
          </button>
        {:else}
          <!-- Aceptar -->
          <button
            type="button"
            class="inline-flex items-center justify-center rounded-full border px-2 py-1 text-xs"
            on:click={handleAccept}
          >
            <span class="material-icons text-base">check</span>
          </button>
          <!-- Cancelar / popUp -->
          <button
            type="button"
            class="inline-flex items-center justify-center rounded-full border px-2 py-1 text-xs"
            on:click={handleCancelClick}
          >
            <span class="material-icons text-base">add</span>
          </button>
        {/if}

        <!-- Botón accordion -->
        <button
          type="button"
          class="inline-flex items-center justify-center rounded-full border px-1 py-1"
          on:click={toggleAccordion}
        >
          <span class="material-icons text-base">
            {#if expanded}
              arrow_drop_up
            {:else}
              arrow_drop_down
            {/if}
          </span>
        </button>
      </div>
    </div>

    <!-- Accordion -->
    {#if expanded}
      <div class="pt-2 border-t mt-2 space-y-2 text-sm">
        <!-- Contacto si está confirmado -->
        {#if confirmed}
          <div class="flex flex-col gap-1">
            {#if !isUser}
              <span class="flex items-center gap-1">
                <span class="material-icons text-base">account_circle</span>
                <span>{user.fullName}</span>
              </span>
            {/if}
            <span class="flex items-center gap-1">
              <span class="material-icons text-base">mail</span>
              <span>{user.email}</span>
            </span>
          </div>
        {/if}

        <!-- Dirección + descripción -->
        <div class="flex flex-col gap-1">
          <span class="flex items-center gap-1">
            <span class="material-icons text-base">house</span>
            {#if service.homeService}
              <span>{locationText}</span>
            {:else}
              <span>{$t('service.at-professional-house')}</span>
            {/if}
          </span>

          {#if !isUser}
            <span class="mt-1 text-sm">
              {#if appointment.description}
                {appointment.description}
              {:else}
                {$t('appointment.no-description')}
              {/if}
            </span>
          {/if}
        </div>

        <!-- Info / leer más -->
        <div class="pt-1">
          {#if isUser}
            <button
              type="button"
              class="inline-flex items-center gap-1 text-xs underline"
              on:click={handleInfo}
            >
              <span class="material-icons text-base">info</span>
              {$t('appointment.info')}
            </button>
          {:else if longDescription}
            <button
              type="button"
              class="inline-flex items-center gap-1 text-xs underline"
              on:click={handleReadMore}
            >
              <span class="material-icons text-base">info</span>
              {$t('read-more')}
            </button>
          {/if}
        </div>
      </div>
    {/if}
  </div>
</div>
