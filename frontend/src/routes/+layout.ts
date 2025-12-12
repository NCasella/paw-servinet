import type { User } from "$models/User";
import { getCurrentUser } from "$services/userService";

export const ssr = false;
	// This can be false if you're using a fallback (i.e. SPA mode)
export const prerender = false;


import type { LayoutLoad } from './$types';

export const load: LayoutLoad = async ({ fetch }) => {
    
    const currentUser = await getCurrentUser(fetch).catch(() => null);
  return { currentUser };
};
	
