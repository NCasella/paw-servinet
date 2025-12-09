import { goto } from "$app/navigation";
import { base } from "$app/paths";
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

const DEFAULT_PAGE_VALUE = 1

/* Default value = 1 */
export function getPageNumFromParam() :number {
    try {
        const pageNum = Number( page.url.searchParams.get('status'))
        return pageNum
    } catch {
        return DEFAULT_PAGE_VALUE
    }
}

export function getPath(url:string) {
    return base+url
}

export function navTo(url:string) {
    goto(base+url)
}