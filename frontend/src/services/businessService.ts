import { Business, type BusinessUpdateInfo } from "$models/Business";
import type { BusinessForm } from "$models/forms/BusinessCreationForm";
import { parsePagedResponse, type PagedResult } from "$models/PagedList";
import type { Service } from "$models/Service";
import { POST, GET, getNewIdFromPostResponse, DELETE, PATCH } from "$utils/apiFetch";
import { getCurrentUser } from "./userService";

export async function createBusiness(form:BusinessForm) :Promise<number> {
    const response = await POST("businesses",form, {
        contentType: "business-creation" 
    })  
    return getNewIdFromPostResponse(response)
}

export async function getUserBusinesses(pageNum:number) : Promise<PagedResult<Business>> {
    const user = await getCurrentUser().catch( (e) => e)
        
    const response = await GET(`businesses?ownerId=${user.userId}&page=${pageNum}`, 
        {contentType: "business-list"} ) 
    
    return parsePagedResponse(response, Business)
}

export async function getBusinessById(businessId: number) : Promise<Business> {
    const response = await GET(`businesses/${businessId}`,
        {contentType: "business-info"}
    )
    console.log( JSON.stringify(response))
    return Business.fromJson(response)
}

export async function getServiceBusinesses(serviceList:Service[]) :Promise<Map<number,Business>>{
    const ids = [...new Set(serviceList.map(s => s.businessId))];

  const businesses = await Promise.all(
    ids.map(id => getBusinessById(id))
  );
console.log(businesses, ids)
  return new Map(
    businesses.map((business, i) => [ids[i], business])
  );
}


export async function deleteBusiness(businessId:number) {
    await DELETE(`businesses/${businessId}`)
}

export async function updateBusiness(businessId:number, form:BusinessUpdateInfo ) {
    PATCH(`businesses/${businessId}`, form, {contentType:"business-update"})
}