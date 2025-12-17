import { readPageFromUrl } from '$lib/navigation/changePage.js';
import { StatusCodes } from '$models/exceptions/statusCodesEnum.js';
import { getCurrentUser } from '$services/userService.js';
import { error } from '@sveltejs/kit';

export async function load({url}) {
    let pageNum = readPageFromUrl(url)
    if (Number.isNaN(pageNum))
        error(StatusCodes.BAD_REQUEST)

    const user = await getCurrentUser().catch(()=> error(StatusCodes.UNAUTHORIZED))

    return {pageNum}
}