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

const DEFAULT_PAGE_VALUE :number = 1

/* Default value = 1 */
export function getPageNumFromParam() :number {
    try {
        const pageNum = Number( page.url.searchParams.get('page')) ?? 1
        return pageNum
    } catch {
        return DEFAULT_PAGE_VALUE
    }
}

export function getPath(url:string) {
    return base+url
}

export async function navTo(url:string) {
    await goto(base+url)
}

export function goBack(backupUrl: string) {
    history.length > 1 ? history.back() : navTo(backupUrl);
  }
