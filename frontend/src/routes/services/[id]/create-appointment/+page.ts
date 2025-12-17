import { StatusCodes } from "$models/exceptions/statusCodesEnum";
import { getBusinessById } from "$services/businessService";
import { getServiceById } from "$services/serviceService";
import { getCurrentUser } from "$services/userService";
import { error } from "@sveltejs/kit";

export async function load({params, url, fetch}) {
    const serviceId = Number(params.id);

  if (Number.isNaN(serviceId)) {
    throw error(StatusCodes.NOT_FOUND)//, { message: 'id must be a number' });
  }
    
    const user = await getCurrentUser().catch(()=> error(StatusCodes.UNAUTHORIZED));
    const service = await getServiceById(serviceId,fetch).catch(()=> error(StatusCodes.NOT_FOUND))
    const business = await getBusinessById(service.businessId,fetch).catch(()=> error(StatusCodes.NOT_FOUND))   
    if ( business.isOwner(user.userId)) error(StatusCodes.FORBIDDEN)

    return {
        user,
        service,
        business,
        serviceId
    }
}