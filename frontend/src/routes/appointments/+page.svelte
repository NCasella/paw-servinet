<script>
	import AppoinmentList from "$lib/components/appointment/AppoinmentList.svelte";
	import { AppointmentView } from "$models/enums/AppointmentStatus";


</script>
<AppoinmentList view={AppointmentView.USER} businessId={-1} />