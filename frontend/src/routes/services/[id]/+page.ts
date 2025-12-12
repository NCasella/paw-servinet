import { getServiceById } from "$services/serviceService";
import type { PageLoad } from "./$types";

import { error } from '@sveltejs/kit';

export async function load({ params }) {
  const serviceId = Number(params.id);

  if (Number.isNaN(serviceId)) {
    throw error(400)//, { message: 'id must be a number' });
  }

  try {
    const service = await getServiceById(serviceId);
    return { service: service };
  } catch {
    throw error(404)//, { message: 'Service not found' });
  }
}

