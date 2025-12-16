import { DELETE, GET, getNewIdFromPostResponse, PATCH, POST } from "$utils/apiFetch";
import { Service } from "$models/Service";
import { ServiceForm, type ServiceUpdateForm } from "$models/forms/ServiceCreationForm";
import {uploadImage} from "./imageService";
import type {PagedResult} from "$models/PagedList";
import {isLastPage, parsePagedResponse} from "$models/PagedList";
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
} = {}, fetchFn?: typeof fetch): Promise<PagedResult<Service>> {

    const query = new URLSearchParams();

    Object.entries(params).forEach(([key, value]) => {
        if (value && value !== "") {
            query.append(key, String(value));
        }
    });

    const response = await GET(
        `services?${query.toString()}`,
        { contentType: "service-list",
          fetchFn: fetchFn
         }
    );

    return parsePagedResponse(response, Service);
}

export async function getServiceById(serviceId:number, fetchFn?: typeof fetch) :Promise<Service> {
    const response = await GET(`services/${serviceId}`,
        { contentType: "service-info",
          fetchFn: fetchFn
        }
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

export async function getAllBusinessServices(businessId: number): Promise<number[]> {
  const ids: number[] = [];
  let page = 1;
  let services: PagedResult<Service>;

  while (true) {
    services = await getServices({ businessId, page });
    services.items.forEach((s) => ids.push(s.serviceId));

    if (isLastPage(page, services)) break;
    page++;
  }

  return ids;
}

export async function updateService(
  serviceId: number,
  form: ServiceUpdateForm
) {
  return await PATCH(`services/${serviceId}`, form, {
    contentType: "service-update",
  });
}