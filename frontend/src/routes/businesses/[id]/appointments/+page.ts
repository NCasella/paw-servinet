import { readPageFromUrl } from "$lib/navigation/changePage";
import { AppointmentStatus, getAppointmentStatus } from "$models/enums/AppointmentStatus";
import { StatusCodes } from "$models/exceptions/statusCodesEnum";
import { getBusinessById } from "$services/businessService";
import { getCurrentUser } from "$services/userService";
import { error } from "@sveltejs/kit";

export async function load({url, params, fetch}) {
    const businessId = Number(params.id);
    const pageNum = readPageFromUrl(url)

   const status = getAppointmentStatus( url.searchParams.get('status'), AppointmentStatus.PENDING) 
  if (Number.isNaN(businessId) || Number.isNaN(pageNum) || status == AppointmentStatus.FINISHED) {
    throw error(StatusCodes.BAD_REQUEST)//, { message: 'id must be a number' });
  }

  const business =  await getBusinessById(businessId,fetch).catch(() => error(StatusCodes.NOT_FOUND))
  const user = await getCurrentUser(fetch).catch( ()=> error(StatusCodes.UNAUTHORIZED))

  if ( !business.isOwner(user.userId) ) throw error(StatusCodes.FORBIDDEN)

    return  {business, pageNum}  
}