import { Business } from "$models/Business";
import type { BusinessForm } from "$models/forms/BusinessCreationForm";
import { parsePagedResponse, type PagedResult } from "$models/PagedList";
import type { Review } from "$models/Review";
import type { Service } from "$models/Service";
import { POST, GET, getNewIdFromPostResponse, DELETE, PATCH } from "$utils/apiFetch";
import { getAllServiceReviews, getServiceReviews } from "./reviewsService";
import { getAllBusinessServices } from "./serviceService";
import { getCurrentUser } from "./userService";
import type {BusinessUpdateForm} from "$models/forms/BusinessUpdateForm";

export async function createBusiness(form:BusinessForm) :Promise<number> {
    const response = await POST("businesses",form, {
        contentType: "business-creation" 
    })  
    return getNewIdFromPostResponse(response)
}

export async function getUserBusinesses(userId:number, pageNum:number) : Promise<PagedResult<Business>> {
    const response = await GET(`businesses?ownerId=${userId}&page=${pageNum}`, 
        {contentType: "business-list"} ) 
    
    return parsePagedResponse(response, Business)
}


export async function getCurrentUserBusinesses(pageNum:number) : Promise<PagedResult<Business>> {
    const user = await getCurrentUser().catch( (e) => e)
        
    const response = await GET(`businesses?ownerId=${user.userId}&page=${pageNum}`, 
        {contentType: "business-list"} ) 
    
    return parsePagedResponse(response, Business)
}
export async function getBusinessById(businessId: number, fetchFn?: typeof fetch) : Promise<Business> {
    const response = await GET(`businesses/${businessId}`,
        {contentType: "business-info",
          fetchFn: fetchFn
        }
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

export async function updateBusiness(businessId:number, form: BusinessUpdateForm ) {
  await PATCH(`businesses/${businessId}`, form, {contentType:"business-update"})
}


export async function getBusinessReviews(businessId :number) : Promise<Review[]> {
  const serviceIds = await getAllBusinessServices(businessId);

  const reviewsPerService = await Promise.all(
    serviceIds.map((id) => getAllServiceReviews(id))
  );

  return reviewsPerService.flat();
}