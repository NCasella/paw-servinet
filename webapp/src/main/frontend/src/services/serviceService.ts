import { GET } from "$utils/apiFetch";
import { Service } from "$models/Service";

export async function getServiceById(serviceId:number) :Promise<Service> {

    const response = await GET(`/services/${serviceId}`,
        { contentType: "service-info"}
    )
    
    return Service.fromJson(response)
}