import { DELETE, GET, getNewIdFromPostResponse, POST } from "$utils/apiFetch";
import { Service } from "$models/Service";
import { ServiceForm } from "$models/forms/ServiceCreationForm";
import { postImage } from "./imageService";
import { DefaultImg } from "$models/enums/DefaultImg";
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

export async function createService(form:ServiceForm, image: File | null) :Promise<number> {
    form.imageId = await postImage(image, 'service')

    const response = await POST("services",form, {
            contentType: "service-creation"
    });
    return getNewIdFromPostResponse(response)
}



export async function deleteService(serviceId:number) :Promise<void>  {
    await DELETE(`services/${serviceId}`)
}
