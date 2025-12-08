import { GET, getNewIdFromPostResponse, POST } from "$utils/apiFetch";
import { Service } from "$models/Service";
import { ServiceForm } from "$models/forms/ServiceCreationForm";
import { number } from "zod";

export async function getServiceById(serviceId:number) :Promise<Service> {

    const response = await GET(`services/${serviceId}`,
        { contentType: "service-info"}
    )
    
    return Service.fromJson(response)
}

export async function createService(form:ServiceForm) :Promise<number> {
    const response = await POST("services",form, {
            contentType: "service-creation" 
    });
    return getNewIdFromPostResponse(response)
}