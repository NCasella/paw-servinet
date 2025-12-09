import {DELETE, GET} from "$utils/apiFetch";
import { Service } from "$models/Service";
import type {PagedResult} from "$models/PagedList";
import {parsePagedResponse} from "$models/PagedList";

export async function getServices(params: {
    businessId?: number;
    category?: string;
    neighbourhoods?: string;
    rating?: number;
    searchQuery?: string;
    orderFilters?: string;
    homeServiceFilter?: boolean;
    page?: number;
} = {}): Promise<PagedResult<Service>> {

    const query = new URLSearchParams();

    Object.entries(params).forEach(([key, value]) => {
        if (value && value !== "") {
            query.append(key, String(value));
        }
    });

    const response = await GET(
        `services?${query.toString()}`,
        { contentType: "service-list" }
    );

    return parsePagedResponse(response, Service);
}

export async function getServiceById(serviceId:number) :Promise<Service> {
    const response = await GET(`services/${serviceId}`,
        { contentType: "service-info"}
    )
    
    return Service.fromJson(response)
}

export async function deleteService(serviceId:number) :Promise<void>  {
    await DELETE(`services/${serviceId}`)
}