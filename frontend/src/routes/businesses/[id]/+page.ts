import type { User } from "$models/User.js";
import { getBusinessById } from "$services/businessService";
import { getServices } from "$services/serviceService.js";
import { getCurrentUser } from "$services/userService.js";
import { error } from "@sveltejs/kit";

export async function load({params, fetch}) {
    
    try {
        const businessId = parseInt(params.id)
        let business = await getBusinessById(businessId, fetch);
        const user: User | null = await getCurrentUser(fetch).catch(() => null);
        const isOwner = user ? business.isOwner(user.userId) : false;
        let servicesList = await getServices({ businessId, page: 1 }, fetch);
        return {
            business,
            isOwner,
            servicesList
        }
    } catch {
        error(404)
    }
    
}