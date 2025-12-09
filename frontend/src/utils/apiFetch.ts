import { getAccessToken } from "$stores/auth"; 

export type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

const BASE_URL = import.meta.env.VITE_API_BASE_URL

export interface TResponse {
  headers: Headers,
  body: string
}

interface FetchOptions<TBody> {
  method?: HttpMethod;
  body?: TBody;
  contentType?: string; 
  headers?: Record<string, string>;
  withAuth?: boolean; // para saltearnos AUTH 
}

export interface FetchError extends Error {
  status?: number;
}

export function isFetchError(e: unknown): e is FetchError {
  return typeof e === "object" && e !== null && "status" in e;
}


export async function apiFetch<TResponse = any, TBody = any>(
  url: string,
  options: FetchOptions<TBody> = {}
): Promise<TResponse> {
  const {
    method = "GET",
    body,
    contentType,
    headers = {},
    withAuth = true 
  } = options;

  let mediaTypeHeader = method=="GET"? "Accept" : "Content-Type"

  const finalHeaders: Record<string, string> = {
    [mediaTypeHeader]: contentType
      ? `application/vnd.servinet.${contentType}.v1+json`
      : "multipart/form-data" ,
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

  const text = await response.text();
  let parsed: TResponse | null = null;

  if (text) {
    try {
      parsed = JSON.parse(text) as TResponse;
    } catch {
      // body no era JSON, lo dejamos en null
      parsed = null;
    }
  }

  return {
    headers: response.headers,
    body: parsed
  } as TResponse;
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


export function getNewIdFromPostResponse(response:TResponse) :number {
    const appUrl = response.headers.get('Location')
    if (appUrl) return extractLastId(appUrl);
    throw Error("id not found from POST")
  //todo tiro statusCodeException en apiFetch
}

function extractLastId(url: string): number {
  try {
    const path = new URL(url).pathname; 
    const parts = path.split("/").filter(Boolean); 
    const last = parts[parts.length - 1];

    const id = Number(last);
    if( !Number.isNaN(id) ) return id;
    throw new Error("Last path segment is not a number");
  
  } catch {
    throw new Error("Couldn't extract id from url");
  }
}