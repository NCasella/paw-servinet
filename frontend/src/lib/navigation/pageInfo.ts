import { page } from "$app/state";
import { InvalidUrlParamError } from "$models/exceptions/InvalidUrlParamError";

export function getParamIdFromUrl() :number {
    try {
        let paramId = Number( page.params.id)
        return paramId
    } catch {
        throw new InvalidUrlParamError("id must be a number") 
    }
    
}