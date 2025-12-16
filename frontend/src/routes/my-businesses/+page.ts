import { setPage } from "$lib/navigation/changePage.js";
import { navTo } from "$lib/navigation/pageInfo";
import { StatusCodes } from "$models/exceptions/statusCodesEnum.js";
import { getBusinessById, getUserBusinesses } from "$services/businessService"
import { getCurrentUser } from "$services/userService";
import { error } from "@sveltejs/kit";

export async function load({url, fetch}) {
    const page = url.searchParams.get('page') ?? "1";

  if (Number.isNaN(page)) {
    throw error(400)//, { message: 'page must be a number' });
  }
    const pageNum= parseInt(page)
  const user = await getCurrentUser().catch( ()=> error(StatusCodes.UNAUTHORIZED))

  const pagedList = await getUserBusinesses(user.userId, pageNum)
        
  
    if ( pagedList.items.length==0) {
        if ( pageNum!= 1 ) navTo("/my-businesses")
        else navTo("/create-business")
    }
return { pagedList, pageNum}

}