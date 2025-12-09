<script lang="ts">
    import type { Service } from "$models/Service";
	import { afterNavigate, goto } from "$app/navigation";
	import { page } from "$app/stores";
	import { Appointment } from "$models/Appointment";
	import { AppointmentStatus, AppointmentView, getAppointmentStatus } from "$models/enums/AppointmentStatus";
	import { onMount } from "svelte";
    import { isLastPage, type PagedResult } from "$models/PagedList";
	import { getCurrentUser } from "$services/userService";
	import { cancelAppointment, confirmAppointment, denyAppointment, getAppointmentsPagedList } from "$services/appointmentService";
    import {t} from '$i18'    
	import Spinner from "$lib/components/global/Spinner.svelte";
	import AppointmentCard from "$lib/components/appointment/AppointmentCard.svelte";
    import { User } from "$models/User";
	import { getAppointmentServices } from "$services/serviceService";
	import type { Business } from "$models/Business";
	import { getServiceBusinesses } from "$services/businessService";
    $: status = getAppointmentStatus( $page.url.searchParams.get('status'), AppointmentStatus.PENDING);
    
    $: pageNum =  getPageNumFromParam()
    let appoinmentList :PagedResult<Appointment>
    let user :User
    $: loading = true
    let serviceList: Map<number, Service>
    let businessList: Map<number,Business>
    onMount( loadData )
    
    async function loadData() {
        //status = getAppointmentStatus( $page.url.searchParams.get('status'), AppointmentStatus.PENDING);
        setStatusBtn()
        console.log(status)
        loading = true
        
        try {
            user = await getCurrentUser()
            appoinmentList = await getAppointmentsPagedList(user.userId, status, AppointmentView.USER )
            serviceList = await getAppointmentServices( appoinmentList.items ) 
            if ( status==AppointmentStatus.CONFIRMED)
                businessList = await getServiceBusinesses(Object.values(serviceList))
        }  finally {
            loading = false
        }
    }

    function getService(app:Appointment) :Service {
        const service = serviceList.get(app.serviceId)
        return service? service: {} as Service
    }
    function getBusiness(app: Appointment): Business | undefined {
    return businessList?.get(getService(app)?.businessId ?? undefined);
    }

  import ConfirmModal from '$lib/components/global/modal/ConfirmModal.svelte';
	import { boolean, type number } from "zod";
	import { getPageNumFromParam } from "$lib/navigation/pageInfo";
	import Title from "$lib/components/global/Title.svelte";
	import Icon from "$icons";

  let showCancelModal = false;
  let selectedAppointment: Appointment | null = null;

  function openCancel(appointment: Appointment) {
    selectedAppointment = appointment;
    showCancelModal = true;
  }

  async function confirmCancel() {
    if (!selectedAppointment) return;
    if ( status == AppointmentStatus.PENDING) 
        await denyAppointment(selectedAppointment.appointmentId)
    else
        await cancelAppointment(selectedAppointment.appointmentId);
    showCancelModal = false;
    selectedAppointment = null; // todo: desaparece? 
    removeAppointmentFromList()
  }

  async function removeAppointmentFromList() {
    // todo
    if ( !isLastPage(pageNum, appoinmentList) )
        loadData()
  }

  async function acceptAppointment(a:Appointment) {
    try {
        await confirmAppointment(a.appointmentId)
        loading = true
    } finally {
        loading = false
    }
  }


  let history :boolean 
  let confirmed :boolean
  let notConfirmed :boolean

  function setStatusBtn() {
history  = status == AppointmentStatus.FINISHED;
confirmed = status == AppointmentStatus.CONFIRMED;
notConfirmed = !confirmed;
  }

  // URLs externas (vos las seteás en el padre)

  async function changeStatusParam(newStatus:AppointmentStatus) {
    const url = new URL($page.url); // copiamos la URL actual
    url.searchParams.set('status', newStatus);

    goto(url.toString(), {
        replaceState: false // evita agregar historial en el navegador
    });
    status = newStatus
    await loadData()
  }
  
</script>


{#if loading}
    <Spinner/>
{:else if appoinmentList && serviceList}
<div class="w-full flex flex-col gap-4">
    <header class="mx-8 flex place-content-between items-baseline">
  {#if history}
    
    <Title text={$t('appointments.history')}/>

    
      <button onclick={()=>changeStatusParam(AppointmentStatus.CONFIRMED)}
        class="px-4 py-2 rounded-full bg-slate-200 hover:bg-slate-300 transition text-sm font-medium"
      >
        {$t('appointments.next-appointments')}
      </button>
    

  {:else}
    <Title text={$t("appointments.my-appointments") }/>

    <div class="flex items-center gap-3">

      <!-- Botón historial -->
        <button onclick={() => changeStatusParam(AppointmentStatus.FINISHED)}
          class="p-2 rounded-full bg-slate-200 hover:bg-slate-300 transition"
          title={$t('appointments.history')}
        >
          <Icon name="history" />
        </button>

      <!-- Switch confirmados / solicitados -->
      <div class="flex border rounded-full overflow-hidden text-sm font-medium">
        <!-- Confirmados -->
          <button onclick={()=>changeStatusParam(AppointmentStatus.CONFIRMED)}
            class="px-4 py-2 transition"
            class:bg-indigo-600={confirmed}
            class:text-white={confirmed}
            class:bg-slate-100={!confirmed}
            class:text-slate-700={!confirmed}
          >
            {$t('appointments.next')}
            <!--(<span>{/* acá ponés el número desde tu padre */}</span>)-->
          </button>

        <!-- Solicitados -->
        <button onclick={async ()=>changeStatusParam(AppointmentStatus.PENDING)}
            class="px-4 py-2 transition"
            class:bg-indigo-600={notConfirmed}
            class:text-white={notConfirmed}
            class:bg-slate-100={!notConfirmed}
            class:text-slate-700={!notConfirmed}
          >
            {$t('appointments.requested')}
            <!--(<span>{/* número */}</span>)-->
          </button>
      </div>
    </div>
  {/if}
  </header>
</div>
    {#each appoinmentList.items as app }
        <AppointmentCard appointment={app} user={user} service={getService(app)} status={status} business={getBusiness(app)}   onRequestCancel={openCancel}
           onAccept={acceptAppointment} view={AppointmentView.USER}  />    
    {/each}
{:else}
 error
{/if}

<ConfirmModal
  open={showCancelModal}
  title={$t('popup.appointment.title')}
  message={$t('popup.appointment.message')}
  cancelLabel={$t('service.cancel')}
  confirmLabel={$t('popup.appointment.cancel')}
  on:cancel={() => (showCancelModal = false)}
  on:confirm={confirmCancel}
/>
