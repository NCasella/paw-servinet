import type { AppointmentForm } from "$models/forms/AppointmentCreationForm";
import { POST, type TResponse } from "$utils/apiFetch";
import { getCurrentUser } from "./userService";

export async function createAppointment(form: AppointmentForm) :Promise<number> {
    const user = await getCurrentUser()
    form.userId = user.userId
    
    const response :TResponse = await POST("appointments", form, {
        contentType: "appointment-creation"
    })   

    const appUrl = response.headers.get('Location')
    if (appUrl) return extractLastId(appUrl);
    throw Error("id not found from POST")
    //tiro statusCodeException en apiFetch
}

function extractLastId(url: string): number {
  try {
    const path = new URL(url).pathname; 
    const parts = path.split("/").filter(Boolean); 
    const last = parts[parts.length - 1];

    const id = Number(last);
    if( !Number.isNaN(id) ) return id;
    throw new Error("Last path segment is not a number");
  
  } catch {
    throw new Error("Couldn't extract id from url");
  }
}
