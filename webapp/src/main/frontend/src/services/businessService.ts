import type { BusinessForm } from "$models/forms/BusinessCreationForm";
import { POST } from "$utils/apiFetch";

export async function createBusiness(form:BusinessForm) {
    POST("businesses",form, {
        //contentType: "" //todo,
    })  
    
}