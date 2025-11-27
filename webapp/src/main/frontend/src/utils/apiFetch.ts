import { getAccessToken } from "$stores/auth"; 

export type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
const BASE_URL = import.meta.env.VITE_API_BASE_URL

interface FetchOptions<TBody> {
  method?: HttpMethod;
  body?: TBody;
  headers?: Record<string, string>;
  withAuth?: boolean; // para saltearnos AUTH 
}

export interface FetchError extends Error {
  status?: number;
}

export async function apiFetch<TResponse = any, TBody = any>(
  url: string,
  options: FetchOptions<TBody> = {}
): Promise<TResponse> {
  const {
    method = "GET",
    body,
    headers = {},
    withAuth = true
  } = options;

  const finalHeaders: Record<string, string> = {
    "Content-Type": "application/json",
    ...headers
  };

  const token = withAuth ? getAccessToken() : null;
  if (token) {
    finalHeaders["Authorization"] = `Bearer ${token}`;
  }
console.log( BASE_URL+url)
  const response = await fetch(BASE_URL+url, {
    method,
    headers: finalHeaders,
    body: body ? JSON.stringify(body) : undefined,
  });

  if (!response.ok) {
      //if ( response.status == 401 )
      //  refreshToken() y rehago request
    const err: FetchError = new Error(`Request failed: ${response.status}`);
    err.status = response.status;
    throw err;
  }

  // Try parsing JSON, but allow empty responses (204)
  try {
    return await response.json();
  } catch {
    return {} as TResponse;
  }
}


export const GET = <T = any>(
  url: string,
  options: FetchOptions<never> = {}
) => apiFetch<T, never>(url, { ...options, method: "GET" });

export const POST = <T = any, B = any>(
  url: string,
  body: B,
  options: FetchOptions<B> = {}
) => apiFetch<T, B>(url, { ...options, method: "POST", body });

export const PATCH = <T = any, B = any>(
  url: string,
  body: B,
  options: FetchOptions<B> = {}
) => apiFetch<T, B>(url, { ...options, method: "PATCH", body });

export const PUT = <T = any, B = any>(
  url: string,
  body: B,
  options: FetchOptions<B> = {}
) => apiFetch<T, B>(url, { ...options, method: "PUT", body });

export const DELETE = <T = any>(
  url: string,
  options: FetchOptions<never> = {}
) => apiFetch<T, never>(url, { ...options, method: "DELETE" });
