import { readPageFromUrl } from "$lib/navigation/changePage";
import { StatusCodes } from "$models/exceptions/statusCodesEnum";
import { getBusinessById } from "$services/businessService";
import { getCurrentUser } from "$services/userService";
import { error } from "@sveltejs/kit";

export async function load({url, params, fetch}) {
    const businessId = Number(params.id);
    const pageNum = readPageFromUrl(url)

  if (Number.isNaN(businessId) || Number.isNaN(pageNum) ) {
    throw error(400)//, { message: 'id must be a number' });
  }


  const business =  await getBusinessById(businessId,fetch).catch(() => error(StatusCodes.BAD_REQUEST))
  const user = await getCurrentUser(fetch).catch( ()=> error(StatusCodes.UNAUTHORIZED))

  if ( !business.isOwner(user.userId) ) throw error(StatusCodes.FORBIDDEN)

    return  {business, pageNum}  
}