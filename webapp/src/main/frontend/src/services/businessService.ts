import { Business } from "$models/Business";
import type { BusinessForm } from "$models/forms/BusinessCreationForm";
import { parsePagedResponse, type PagedResult } from "$models/PagedList";
import { POST, GET } from "$utils/apiFetch";
import { getCurrentUser } from "./userService";

export async function createBusiness(form:BusinessForm) {
    POST("businesses",form, {
        contentType: "business-creation" 
    })  
}

export async function getUserBusinesses() : Promise<PagedResult<Business>> {
    const user = await getCurrentUser().catch( (e) => e)
        
    const response = await GET(`businesses?ownerId=${user.userId}`, 
        {contentType: "business-list"} ) 
    
    return parsePagedResponse(response, Business)
}