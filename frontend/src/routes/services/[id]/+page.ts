import { StatusCodes } from "$models/exceptions/statusCodesEnum";
import { getServiceById } from "$services/serviceService";
import type { PageLoad } from "./$types";

import { error } from '@sveltejs/kit';

export async function load({ params, fetch }) {
  const serviceId = Number(params.id);

  if (Number.isNaN(serviceId)) {
    throw error(StatusCodes.NOT_FOUND)//, { message: 'id must be a number' });
  }

  try {
    const service = await getServiceById(serviceId, fetch);
    return { service: service };
  } catch(e) {
    //console.log(e)
    error(404, { message: 'Service not found' });
  }
}

