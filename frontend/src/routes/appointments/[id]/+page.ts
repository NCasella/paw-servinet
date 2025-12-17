import { StatusCodes } from '$models/exceptions/statusCodesEnum.js'
import { getAppointmentById } from '$services/appointmentService'
import { getBusinessById } from '$services/businessService.js'
import { getServiceById } from '$services/serviceService'
import { getCurrentUser } from '$services/userService'
import { error } from '@sveltejs/kit'

export async function load({url, params, fetch}) {
    const appId = Number(params.id )
    if ( !appId) error(StatusCodes.BAD_REQUEST)
    const user = await getCurrentUser(fetch).catch( ()=> error(StatusCodes.UNAUTHORIZED))
    
    const appointment = await getAppointmentById(appId).catch((e)=> error(e.status))
    // con buscar el appointment ya se valida si owner o user, pero solo queremos q lo vea el user:
    if (user.userId != appointment.userId) error(StatusCodes.FORBIDDEN)
    try {
        const service = await getServiceById(appointment.serviceId, fetch)
        const business = await getBusinessById(service.businessId, fetch)
        
        return {
            appointment,
            service,
            user,
            business
        }
    } catch {
        error(StatusCodes.BAD_REQUEST)
    }

}