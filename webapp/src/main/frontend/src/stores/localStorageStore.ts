import { writable, type Writable } from 'svelte/store';
import { browser } from '$app/environment';

export function localStorageStore<T>(key: string, initial: T): Writable<T> {
  const stored = browser ? localStorage.getItem(key) : null;
  const startValue = stored ? (JSON.parse(stored) as T) : initial;

  const store = writable<T>(startValue);

  if (browser) {
    store.subscribe((value) => {
      localStorage.setItem(key, JSON.stringify(value));
    });
  }

  return store;
}
