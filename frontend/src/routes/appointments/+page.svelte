<script lang="ts">
    import type { Service } from "$models/Service";
	import { goto } from "$app/navigation";
	import { page } from "$app/stores";
	import { Appointment } from "$models/Appointment";
	import { AppointmentStatus, AppointmentView, getAppointmentStatus } from "$models/enums/AppointmentStatus";
	import { onMount } from "svelte";
    import type { PagedResult } from "$models/PagedList";
	import { getCurrentUser } from "$services/userService";
	import { getAppointmentsPagedList } from "$services/appointmentService";
    import {t} from '$i18'    
	import Spinner from "$lib/components/global/Spinner.svelte";
	import AppointmentCard from "$lib/components/appointment/AppointmentCard.svelte";
    import { User } from "$models/User";
	import { getAppointmentServices } from "$services/serviceService";
	import type { Business } from "$models/Business";
	import { getServiceBusinesses } from "$services/businessService";
    $: status = getAppointmentStatus( $page.url.searchParams.get('status'), AppointmentStatus.PENDING);
    
    
    let appoinmentList :PagedResult<Appointment>
    let user :User
    let loading = true
    let serviceList: Map<number, Service>
    let businessList: Map<number,Business>
    onMount( async () => {
        try {
            user = await getCurrentUser()
            appoinmentList = await getAppointmentsPagedList(user.userId, status, AppointmentView.USER )
            serviceList = await getAppointmentServices( appoinmentList.items ) 
            if ( status==AppointmentStatus.CONFIRMED)
                businessList = await getServiceBusinesses(Object.values(serviceList))
        }  finally {
            loading = false
        }

    })

    function getService(app:Appointment) :Service | undefined {
        return serviceList.get(app.serviceId)
    }
    function getBusiness(app: Appointment): Business | undefined {
    return businessList?.get(getService(app)?.businessId ?? -1);
    }

</script>


{#if loading}
    <Spinner/>
{:else if appoinmentList && serviceList}
    {#each appoinmentList.items as app }
        <AppointmentCard appointment={app} user={user} service={getService(app)} status={status} business={getBusiness(app)} />    
    {/each}
{:else}
 error
{/if}