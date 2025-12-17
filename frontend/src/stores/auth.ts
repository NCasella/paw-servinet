import { get, type Writable } from 'svelte/store';
import { localStorageStore } from '$stores/localStorageStore'

export type AuthState = {
  accessToken: string | null;
  refreshToken: string | null;
};

const initialState: AuthState = {
  accessToken: null,
  refreshToken: null
};


export const auth: Writable<AuthState> = localStorageStore<AuthState>('authStore', initialState);

export function setTokens(tokens: Partial<AuthState>) {
  auth.update((current) => ({
    ...current,
    ...tokens
  }));
}

export function clearTokens() {
  auth.set({ ...initialState });
}

export function getAccessToken(): string | null {
  return get(auth).accessToken;
}

export function getRefreshToken(): string | null {
  return get(auth).refreshToken;
}