<script lang="ts">
    import type { Service } from "$models/Service";
	import { afterNavigate, goto } from "$app/navigation";
	import { page } from "$app/stores";
	import { Appointment } from "$models/Appointment";
	import { AppointmentStatus, AppointmentView, getAppointmentStatus } from "$models/enums/AppointmentStatus";
	import { onMount } from "svelte";
    import { isLastPage, type PagedResult } from "$models/PagedList";
	import { getAppointmentClients, getCurrentUser } from "$services/userService";
	import { cancelAppointment, confirmAppointment, denyAppointment, getAppointmentsPagedList } from "$services/appointmentService";
    import {t} from '$i18'    
	import Spinner from "$lib/components/global/Spinner.svelte";
	import AppointmentCard from "$lib/components/appointment/AppointmentCard.svelte";
    import { User } from "$models/User";
	import { getAppointmentServices } from "$services/serviceService";
	import type { Business } from "$models/Business";
	import { getServiceBusinesses } from "$services/businessService";
    const status = $derived(
  getAppointmentStatus($page.url.searchParams.get("status"), AppointmentStatus.PENDING)
);
const pageNum = $derived(Number($page.url.searchParams.get("page") ?? 1))

    
    /* Params */
    let {view, business} = $props()
    
    let appoinmentList :PagedResult<Appointment> = $state({} as PagedResult<Appointment>)
    let user :User
  
    let serviceList: Map<number, Service> = $state(new Map<number, Service>)
    let businessList: Map<number,Business>
    let clientList: Map<number,ContactInfo>
    let contactInfo :ContactInfo
    
    let loading = $state(true);

// evita loops/race conditions
let requestId = 0;

$effect(() => {
  // dependencias: pageNum y status (y view/business si aplica)
  const p = pageNum;
  const s = status;
  const v = view;
  const bId = business?.businessId;

  // correr async sin bloquear el efecto
  void (async () => {
    const id = ++requestId;
    loading = true;
    try {
      await loadData();
    } finally {
      // solo apagar loading si esta request sigue siendo la última
      if (id === requestId) loading = false;
    }
  })();
});

    
    async function loadBusinessAppointments() {
        appoinmentList = await getAppointmentsPagedList(business.businessId, status, AppointmentView.BUSINESS, pageNum )
        serviceList = await getAppointmentServices( appoinmentList.items ) 
        if ( status==AppointmentStatus.CONFIRMED) 
            clientList = await getAppointmentClients(appoinmentList.items)
    }

    async function loadUserAppointments() {
        user = await getCurrentUser()
        appoinmentList = await getAppointmentsPagedList(user.userId, status, AppointmentView.USER, pageNum )
        serviceList = await getAppointmentServices( appoinmentList.items ) 
        if ( status==AppointmentStatus.CONFIRMED) 
            businessList = await getServiceBusinesses(serviceList.values().toArray())

    }

    async function loadData() {
        //status = getAppointmentStatus( $page.url.searchParams.get('status'), AppointmentStatus.PENDING);
        setStatusBtn()
        loading = true
        
        try {
            if ( view == AppointmentView.USER)
                await loadUserAppointments()
            else 
                await loadBusinessAppointments()
            
                if ( appoinmentList.items.length == 0 && pageNum!=1){
                  //pageNum=1
                  setPage(1)
                  //loadData()
                  //const redirectUrl =`/appointments?status=${status}`
                  //if (view == AppointmentView.BUSINESS) await navTo(`/businesses/${business.businessId}${ redirectUrl}`)
                  //else await setPage(1) //navTo(redirectUrl)
                  //pageNum=1
                  // loadData()
                  ////window.location.reload
                  ////console.log(pageNum)
                }
                  
        }  finally {
            loading = false
        }
    }

    function getService(app:Appointment) :Service {
        const service = serviceList.get(app.serviceId)
        return service? service: {} as Service
    }
    function getBusiness(app: Appointment): Business {
        const s = getService(app)
        if ( s==null) return {} as Business
        return businessList.get( s.businessId) ?? {} as Business;
    }

    function getClient(app: Appointment): ContactInfo {
        return clientList.get( app.userId) ?? {} as ContactInfo
    }

    function getContactInfo(a:Appointment) :ContactInfo {
        if ( status != AppointmentStatus.CONFIRMED) return {} as ContactInfo
        if ( view == AppointmentView.BUSINESS) return getClient(a);
        const business = getBusiness(a)
        return { email:  business.email } as ContactInfo
    }

    function getConfirmLabel() {
        return status==AppointmentStatus.CONFIRMED || isUser? 'popup.appointment.cancel' : "popup.appointment.deny"
    }

    function getLabel() {
        return status==AppointmentStatus.CONFIRMED ||  isUser? 'popup.appointment.message':'popup.deny-appointment.message'
    }



  import ConfirmModal from '$lib/components/global/modal/ConfirmModal.svelte';
	import { boolean, email, type number } from "zod";
	import { getPageNumFromParam, getPath, navTo } from "$lib/navigation/pageInfo";
	import Title from "$lib/components/global/Title.svelte";
	import Icon from "$icons";
	import NoResults from "$lib/components/global/pagedResults/NoResults.svelte";
	import PaginationControls from "$lib/components/global/pagedResults/PaginationControls.svelte";
	import type { ContactInfo } from "$models/ContactInfo";
	import { text } from "@sveltejs/kit";
	import { setPage } from "$lib/navigation/changePage";

  let showCancelModal = $state(false);
  let selectedAppointment: Appointment | null = null;

  function openCancel(appointment: Appointment) {
    selectedAppointment = appointment;
    showCancelModal = true;
  }

  async function confirmCancel() {
    if (!selectedAppointment) return;
    try {
      loading = true
      if ( status == AppointmentStatus.PENDING && !isUser) 
          await denyAppointment(selectedAppointment.appointmentId)
      else
          await cancelAppointment(selectedAppointment.appointmentId);
    } finally {
      loading = false
    }
    showCancelModal = false;
    selectedAppointment = null; // todo: desaparece? 
    removeAppointmentFromList()
  }

  async function removeAppointmentFromList() {
    // todo
    if ( !isLastPage(pageNum, appoinmentList) || appoinmentList.items.length == 1)
        loadData()
  }

  async function acceptAppointment(a:Appointment) {
    try {
        loading = true
        await confirmAppointment(a.appointmentId)
        loadData()
    } finally {
        loading = false
    }
  }


  let history :boolean 
  let confirmed :boolean
  let notConfirmed :boolean
  const isUser = view == AppointmentView.USER
  function setStatusBtn() {
history  = isUser && status == AppointmentStatus.FINISHED;
confirmed = status == AppointmentStatus.CONFIRMED;
notConfirmed = !confirmed;
  }

  // URLs externas (vos las seteás en el padre)

  async function changeStatusParam(newStatus:AppointmentStatus) {
    const url = new URL($page.url); // copiamos la URL actual
    url.searchParams.set('status', newStatus);
    url.searchParams.set('page', '1');

    goto(url.toString(), {
        replaceState: false
    });
    //status = newStatus
    //pageNum = 1
    //await loadData()
  }
  
  
</script>


{#if loading}
    <Spinner/>
{:else}
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
      {#if isUser} <Title text={$t("appointments.my-appointments") }/>
      {:else} <Title text={business.businessName}/>
      {/if}

    <div class="flex items-center gap-3">

      <!-- Botón historial -->
       {#if isUser}
        <button onclick={() => changeStatusParam(AppointmentStatus.FINISHED)}
          class="p-2 rounded-full bg-slate-200 hover:bg-slate-300 transition"
          title={$t('appointments.history')}
        >
          <Icon name="history" />
        </button>
        {/if}
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
        <AppointmentCard appointment={app} contactInfo={getContactInfo(app)} service={getService(app)} status={status}  onRequestCancel={openCancel}
           onAccept={acceptAppointment} view={view}  />    
    {/each}
    <PaginationControls
    pagedList={appoinmentList}
    page={pageNum}
    onPageChange={(newPage) => {
        setPage(newPage)
    }}
    />

    {#if appoinmentList.items.length==0}
        <NoResults actionUrl={getPath("/")}  actionLabel={$t("services.look-for-services")}/>
    {/if}
{/if}

<ConfirmModal
  open={showCancelModal}
  title={$t('popup.appointment.title')}
  message={$t(getLabel())}
  cancelLabel={$t('service.cancel')}
  confirmLabel={$t(getConfirmLabel())}
  on:cancel={() => (showCancelModal = false)}
  on:confirm={confirmCancel}
/>
