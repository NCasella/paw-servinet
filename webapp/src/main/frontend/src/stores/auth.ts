import { writable, get } from "svelte/store";

export type AuthState = {
  accessToken: string | null;
  refreshToken: string | null;
};

const initialState: AuthState = {
  accessToken: null,
  refreshToken: null
};

export const auth = writable<AuthState>(initialState);

export function setTokens(tokens: Partial<AuthState>) {
  auth.update((current) => ({
    ...current,
    ...tokens
  }));
}

export function clearTokens() {
  auth.set(initialState);
}

export function getAccessToken(): string | null {
  return get(auth).accessToken;
}
