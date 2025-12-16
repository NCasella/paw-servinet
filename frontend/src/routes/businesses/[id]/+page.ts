import { readPageFromUrl } from "$lib/navigation/changePage";
import { navTo } from "$lib/navigation/pageInfo.js";
import type { User } from "$models/User.js";
import { getBusinessById } from "$services/businessService";
import { getServices } from "$services/serviceService.js";
import { getCurrentUser } from "$services/userService.js";
import { error } from "@sveltejs/kit";

export async function load({url, params, fetch}) {
    
    const pageNum = readPageFromUrl(url)
    try {
        const businessId = parseInt(params.id)
        let business = await getBusinessById(businessId, fetch);
        const user: User | null = await getCurrentUser(fetch).catch(() => null);
        const isOwner = user ? business.isOwner(user.userId) : false;
        let servicesList = await getServices({ businessId, page: pageNum }, fetch);
        if ( servicesList.items.length == 0 && pageNum!=1 ) 
            navTo(`/businesses/${businessId}`)
        return {
            business,
            isOwner,
            servicesList,
            pageNum
        }
    } catch {
        error(404)
    }
    
}