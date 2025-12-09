import { GET, getNewIdFromPostResponse, POST } from "$utils/apiFetch";
import { Service } from "$models/Service";
import { ServiceForm } from "$models/forms/ServiceCreationForm";
import { number } from "zod";
import { postImage } from "./imageService";
import { DefaultImg } from "$models/enums/DefaultImg";

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



