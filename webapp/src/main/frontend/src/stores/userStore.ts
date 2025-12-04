import { writable, get } from "svelte/store";
import type { User } from "$models/User";

export type UserState = {
  user: User | null;
};

const initialState: UserState = {
  user: null
};

export const user = writable<UserState>({ ...initialState });

export function login(loggedUser: User) {
  user.update((state) => ({
    ...state,
    user: loggedUser
  }));
}

export function logout() {
  user.set({ ...initialState });
}

export function getUser(): User | null {
  return get(user).user;
}
