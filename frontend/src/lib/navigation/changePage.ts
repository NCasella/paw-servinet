import { goto } from '$app/navigation';
import { page } from '$app/stores';
import { error } from '@sveltejs/kit';
import { get } from 'svelte/store';

export async function setPage(newPage: number) {
  const $page = get(page);

  const params = new URLSearchParams($page.url.searchParams);
  params.set('page', newPage.toString());

  await goto(`${$page.url.pathname}?${params.toString()}`, {
    replaceState: true, // no ensucia el historial
  });
}

export function readPageFromUrl(url: URL) :number {
    const page = url.searchParams.get('page') ?? "1";
    
      if (Number.isNaN(page)) {
        throw error(400)//, { message: 'page must be a number' });
      }
    return parseInt(page)
}
