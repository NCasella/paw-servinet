import { readPageFromUrl, setPage } from "$lib/navigation/changePage.js";
import { navTo } from "$lib/navigation/pageInfo";
import { StatusCodes } from "$models/exceptions/statusCodesEnum.js";
import { getBusinessById, getUserBusinesses } from "$services/businessService"
import { getCurrentUser } from "$services/userService";
import { error } from "@sveltejs/kit";

export async function load({url, fetch}) {
    
  const user = await getCurrentUser().catch( ()=> error(StatusCodes.UNAUTHORIZED))
  const pageNum = readPageFromUrl(url)
  
  const pagedList = await getUserBusinesses(user.userId, pageNum)      
    
    if ( pagedList.items.length==0) {
        if ( pageNum!= 1 ) navTo("/my-businesses")
        else navTo("/create-business")
    }
return { pagedList, pageNum}

}