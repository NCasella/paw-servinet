import { navTo } from "$lib/navigation/pageInfo"
import { StatusCodes } from "$models/exceptions/statusCodesEnum.js"
import { getCurrentUser } from "$services/userService"
import { error } from "@sveltejs/kit"

export async function load({ fetch }) {
    let user, otherLang
    try {
        user = await getCurrentUser()
        otherLang = user.language === 'es' ? 'en' : 'es'
    } catch {
		error(StatusCodes.UNAUTHORIZED)
    }
    return {
        user: user, otherLang: otherLang
    }
}