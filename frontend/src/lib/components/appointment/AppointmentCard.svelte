<script lang="ts">
  import type { Appointment } from '$models/Appointment';
  import { AppointmentStatus } from '$models/enums/AppointmentStatus';
  import type { User } from '$models/User';
  import type { Service } from '$models/Service';
  import { t } from '$i18';
  import Icon from '$icons';

  // Props principales
  export let appointment: Appointment;
  export let user: User;
  export let service: Service;
  export let status: AppointmentStatus;

  // ¿Es un turno del historial?
  export let history = status === AppointmentStatus.FINISHED;
  // ¿La vista es del usuario (cliente) o del provider?
  export let isUser = false;

  // Labels (si no te los pasan desde afuera, los podés derivar acá)
  export let dayLabel: string = appointment.formatedDate().toDateString(); // ej: "12/08"
  export let dayWithYearLabel: string = '';                  // ej: "12/08/2025"

  // Descripción larga para “leer más” (opcional)
  export let longDescription: string | null = null;

  // URLs
  export let serviceUrl = '';         // /servicio/{serviceid}
  export let renewUrl = '';           // /contratar-servicio/{serviceid}
  export let userAppointmentUrl = ''; // /turno/{serviceid}/{appointmentId}

  // Callbacks que implementás en el padre
  export let onAccept: (appointment: Appointment) => void;
  export let onRequestCancel: (appointment: Appointment) => void;
  export let onReadMore: (appointment: Appointment) => void;
  export let onOpenInfo: (appointment: Appointment) => void;

  // Estado interno: accordion
  let expanded = false;

  // Derivados
  $: confirmed = status === AppointmentStatus.CONFIRMED;
  $: dayText = history && dayWithYearLabel ? dayWithYearLabel : dayLabel;

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
  <div class="flex flex-col w-full">
    <!-- Caja principal (equivalente a .appointment-box) -->
    <div
      class="mt-4 rounded-2xl bg-white shadow-sm px-4 py-2 flex items-center justify-between gap-3"
    >
      <div
        class="grid items-center gap-3 w-full"
        style="grid-template-columns: 150px 170px minmax(0,1fr) 55px auto;"
      >
        <!-- Día -->
        <span class="text-sm font-medium truncate">
          {dayText}
        </span>

        <!-- Horario -->
        <span class="flex items-center gap-1 text-sm text-slate-500">
          <Icon name="schedule" />
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
        <span class="text-xs text-slate-400 text-right">
          #{appointment.appointmentId}
        </span>

        <!-- Botones decisión + accordion -->
        <div class="flex items-center justify-end gap-1">
          {#if history}
            <!-- Renovar -->
            <button
              type="button"
              class="inline-flex items-center justify-center rounded-full border border-slate-200 px-2 py-1 text-xs text-slate-600 hover:bg-slate-50"
              on:click={() => { /* p.ej. navigate(renewUrl) */ }}
              title={$t('appointment.renew') ?? 'Renovar'}
            >
              <Icon name="event-repeat" />
            </button>
          {:else if confirmed || isUser}
            <!-- Solo cancelar -->
            <button
              type="button"
              class="inline-flex items-center justify-center rounded-full border border-red-200 px-2 py-1 text-xs text-red-500 hover:bg-red-50"
              on:click={handleCancelClick}
            >
              <Icon name="delete" />
            </button>
          {:else}
            <!-- Aceptar -->
            <button
              type="button"
              class="inline-flex items-center justify-center rounded-full border border-emerald-200 px-2 py-1 text-xs text-emerald-600 hover:bg-emerald-50"
              on:click={handleAccept}
            >
              <Icon name="check" />
            </button>
            <!-- Cancelar / popUp -->
            <button
              type="button"
              class="inline-flex items-center justify-center rounded-full border border-slate-200 px-2 py-1 text-xs text-slate-600 hover:bg-slate-50"
              on:click={handleCancelClick}
            >
              <Icon name="add" />
            </button>
          {/if}

          <!-- Botón accordion -->
          <button
            type="button"
            class="inline-flex items-center justify-center rounded-full border border-slate-200 w-7 h-7 text-slate-500 hover:bg-slate-50"
            on:click={toggleAccordion}
          >
            <Icon name={'downArrow'} />
          </button>
        </div>
      </div>
    </div>

    <!-- Accordion -->
    {#if expanded}
      <div
        class="bg-slate-100 text-slate-700 rounded-b-2xl -mt-2 pt-4 pb-3 px-4 flex justify-between gap-6 text-sm"
      >
        <!-- Contacto si está confirmado -->
        <div class="flex flex-col gap-2 min-w-[200px] max-w-[260px]">
          {#if confirmed}
            {#if !isUser}
              <span class="flex items-center gap-1">
                <Icon name="account-circle" />
                <span class="truncate">{user.fullName}</span>
              </span>
            {/if}
            <span class="flex items-center gap-1">
              <Icon name="mail" />
              <span class="truncate">{user.email}</span>
            </span>
          {/if}
        </div>

        <!-- Dirección + descripción -->
        <div class="flex-1 flex flex-col gap-2">
          <span class="flex items-center gap-1">
            <Icon name="house" />
            {#if service.homeService}
              <span class="truncate">{locationText}</span>
            {:else}
              <span>{$t('service.at-professional-house')}</span>
            {/if}
          </span>

          {#if !isUser}
            <span
              class="mt-1 text-sm bg-slate-200/80 rounded-2xl px-3 py-2 max-w-[550px] overflow-hidden text-ellipsis whitespace-nowrap"
            >
              {#if appointment.description}
                {appointment.description}
              {:else}
                {$t('appointment.no-description')}
              {/if}
            </span>
          {/if}
        </div>

        <!-- Info / leer más -->
        <div class="flex items-end">
          {#if isUser}
            <button
              type="button"
              class="inline-flex items-center gap-1 text-xs underline"
              on:click={handleInfo}
            >
              <Icon name="info" />
              {$t('appointment.info')}
            </button>
          {:else if longDescription}
            <button
              type="button"
              class="inline-flex items-center gap-1 text-xs underline"
              on:click={handleReadMore}
            >
              <Icon name="info" />
              {$t('read-more')}
            </button>
          {/if}
        </div>
      </div>
    {/if}
  </div>
</div>
