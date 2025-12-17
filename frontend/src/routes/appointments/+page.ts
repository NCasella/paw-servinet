import { readPageFromUrl } from '$lib/navigation/changePage.js';
import { StatusCodes } from '$models/exceptions/statusCodesEnum.js';
import { error } from '@sveltejs/kit';

export async function load({url}) {
    let pageNum = readPageFromUrl(url)
    if (Number.isNaN(pageNum))
        error(StatusCodes.BAD_REQUEST)
    return {pageNum}
}