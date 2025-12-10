import { DELETE, GET, getNewIdFromPostResponse, POST } from "$utils/apiFetch";
import { Service } from "$models/Service";
import { ServiceForm } from "$models/forms/ServiceCreationForm";
import {uploadImage} from "./imageService";
import type {PagedResult} from "$models/PagedList";
import {parsePagedResponse} from "$models/PagedList";
import type { Appointment } from "$models/Appointment";

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
    if(image != null) form.imageId = await uploadImage(image);

    const response = await POST("services",form, {
            contentType: "service-creation"
    });
    return getNewIdFromPostResponse(response)
}



export async function deleteService(serviceId:number) :Promise<void>  {
    await DELETE(`services/${serviceId}`)
}


export async function getAppointmentServices(appointmentsList:Appointment[]) :Promise<Map<number,Service>>{
    const serviceIds = [...new Set(appointmentsList.map(a => a.serviceId))];

  const services = await Promise.all(
    serviceIds.map(id => getServiceById(id))
  );

  return new Map(
    services.map((service, i) => [serviceIds[i], service])
  );
}

