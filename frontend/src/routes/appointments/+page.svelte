<script lang="ts">
	import { goto } from "$app/navigation";
	import { page } from "$app/stores";
	import { Appointment } from "$models/Appointment";
	import { AppointmentStatus, AppointmentView, getAppointmentStatus } from "$models/enums/AppointmentStatus";
	import { base } from "$service-worker";
	import { onMount } from "svelte";
    import type { PagedResult } from "$models/PagedList";
	import { getCurrentUser } from "$services/userService";
	import { getAppointmentsPagedList } from "$services/appointmentService";
    import {t} from '$i18'    
    
    $: status = getAppointmentStatus( $page.url.searchParams.get('status'), AppointmentStatus.PENDING);
    // $: appoinmentList
    let appoinmentList :PagedResult<Appointment>
    onMount( async () => {
        let user = await getCurrentUser()
        appoinmentList = await getAppointmentsPagedList(user.userId, status, AppointmentView.USER )
    })

</script>

