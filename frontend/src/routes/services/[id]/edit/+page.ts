import { StatusCodes } from "$models/exceptions/statusCodesEnum.js";
import type { User } from "$models/User.js";
import { getBusinessById } from "$services/businessService";
import { getServiceById } from "$services/serviceService";
import { getCurrentUser } from "$services/userService";
import { error } from "@sveltejs/kit";

export async function load({fetch, params}) {
  
  const serviceId = parseInt(params.id)
  const user: User = await getCurrentUser(fetch).catch(() => error(StatusCodes.UNAUTHORIZED));
  const service = await getServiceById(serviceId, fetch).catch(() => error(StatusCodes.NOT_FOUND));
  const business = await getBusinessById(service.businessId, fetch).catch(() => error(StatusCodes.NOT_FOUND));
  
  if ( !business.isOwner(user.userId) )
      error(StatusCodes.FORBIDDEN)
    
  return { service: service };
  
}