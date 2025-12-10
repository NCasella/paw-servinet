<script lang="ts">
  import type { Appointment } from '$models/Appointment';
  import { AppointmentStatus } from '$models/enums/AppointmentStatus';
  import type { User } from '$models/User';
  import type { Service } from '$models/Service';
  import { t } from '$i18';
  import Icon from '$icons';
	import { goto } from '$app/navigation';
	import { base } from '$app/paths';
	import { AppointmentView} from '$models/enums/AppointmentStatus';
	import { getPath } from '$lib/navigation/pageInfo';

  // Props principales
  export let appointment: Appointment;
  export let user: User;
  export let service: Service;
  export let status: AppointmentStatus;
  export let business;

  // ¿Es un turno del historial?
  export let history = status === AppointmentStatus.FINISHED;
  
  export let view :AppointmentView
  export let isUser = view == AppointmentView.USER;

  // Labels (si no te los pasan desde afuera, los podés derivar acá)
  export let dayLabel: string = appointment.formatedDate().toDateString(); // ej: "12/08"
  export let dayWithYearLabel: string = '';                  // ej: "12/08/2025"

  // URLs
  export let serviceUrl = getPath(`/services/${service.serviceId}`);         // /servicio/{serviceid}
  export let renewUrl = serviceUrl + '/create-appointment' ;           // /contratar-servicio/{serviceid}
  export let userAppointmentUrl = ''; // /turno/{serviceid}/{appointmentId}

  // Callbacks que implementás en el padre
  export let onAccept: (appointment: Appointment) => Promise<void>;
  export let onRequestCancel: (appointment: Appointment) => void;

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
    goto(`${base}/appointments/${appointment.appointmentId}`)
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
          class="text-sm hover:underline truncate text-primary-500 underline"
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
            <a href={renewUrl}>
            <button
              type="button"
              class="inline-flex items-center justify-center rounded-full border border-slate-200 px-2 py-1 text-xs text-slate-600 hover:bg-slate-50"
            >
              <Icon name="eventRepeat" />
            </button>
            </a>
          {:else if !confirmed && !isUser}
              <!-- Aceptar -->
              <button
                type="button"
                class="inline-flex items-center justify-center rounded-full border border-emerald-200 px-2 py-1 text-xs text-emerald-600 hover:bg-emerald-50"
                on:click={handleAccept}
              >
                <Icon name="check" />
              </button>
            <!-- deny -->
            <button
              type="button"
              class="inline-flex items-center justify-center rounded-full border border-slate-200 px-2 py-1 text-xs text-slate-600 hover:bg-slate-50"
              on:click={handleCancelClick}
            >
              <Icon name="delete" />
            </button>  
          {:else}
            <!-- Cancelar / popUp -->
            <button
              type="button"
              class="inline-flex items-center justify-center rounded-full border border-slate-200 px-2 py-1 text-xs text-slate-600 hover:bg-slate-50"
              on:click={handleCancelClick}
            >
              <Icon name="delete" />
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

          
            <span
              class="mt-1 text-sm bg-slate-200/80 rounded-2xl px-3 py-2 max-w-[550px] overflow-hidden text-ellipsis whitespace-nowrap"
            >
              {#if appointment.description}
                {appointment.description}
              {:else}
                {$t('appointment.no-description')}
              {/if}
            </span>
          
        </div>

        <!-- Info / leer más -->
        <div class="flex items-end">
            <button
              type="button"
              class="btn  bg-slate-200/80"
              on:click={handleReadMore}
            >
              <Icon name="info" />
              {$t('read-more')}
            </button>
          
        </div>
      </div>
    {/if}
  </div>
</div>
