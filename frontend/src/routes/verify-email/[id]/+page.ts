import { navTo } from "$lib/navigation/pageInfo"
import { StatusCodes } from "$models/exceptions/statusCodesEnum.js"
import { getCurrentUser, getCurrentUserContactInfo } from "$services/userService"
import { error } from "@sveltejs/kit"

export async function load({ fetch }) {
    let user, otherLang
    try {
        user = await getCurrentUserContactInfo()
        otherLang = user.language === 'es' ? 'en' : 'es'
    } catch(e) {
        error(StatusCodes.UNAUTHORIZED)
    }
    return {
        user: user, otherLang: otherLang
    }
}