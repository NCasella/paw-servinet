import { goto } from '$app/navigation';
import { page } from '$app/stores';
import { get } from 'svelte/store';

export function setPage(newPage: number) {
  const $page = get(page);

  const params = new URLSearchParams($page.url.searchParams);
  params.set('page', newPage.toString());

  goto(`${$page.url.pathname}?${params.toString()}`, {
    replaceState: true, // no ensucia el historial
  });
}
