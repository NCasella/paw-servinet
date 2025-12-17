import { removeTokens } from "$services/authenticate";
import { getAccessToken, getRefreshToken, setTokens } from "$stores/auth"; 
import { base } from "$app/paths"; 

export type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

export interface TResponse {
  headers: Headers,
  body: string
}

interface FetchOptions<TBody> {
  method?: HttpMethod;
  body?: TBody;
  contentType?: string;
  genericContentType?: string;
  headers?: Record<string, string>;
  withAuth?: boolean; // para saltearnos AUTH
  binary?: boolean;
  fetchFn?: typeof fetch;
}

export class FetchError extends Error {
  status: number;

  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
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
    genericContentType,
    headers = {},
    withAuth = true,
    binary = false,
    fetchFn = fetch
  } = options;

  const finalHeaders: Record<string, string> = { ...headers };
  const token = withAuth ? getAccessToken() : null;
  const refreshToken = withAuth ? getRefreshToken() : null;
  let usingRefresh = false;
  if (token) {
    finalHeaders["Authorization"] = `Bearer ${token}`;
  }else if (refreshToken){
    finalHeaders["Authorization-Refresh-Token"] = `Bearer ${refreshToken}`;
    usingRefresh = true;
  }

  const isFormData = body instanceof FormData;
  if (!isFormData) {
    const mediaType = method == 'GET' ? 'Accept' : 'Content-Type';
    finalHeaders[mediaType] = contentType
        ? `application/vnd.servinet.${contentType}.v1+json`
        : (genericContentType ? genericContentType : "application/json");
  }

  let response = await fetchFn(API_BASE_URL + "/" + url, {
    method,
    headers: finalHeaders,
    body: isFormData ? body : body ? JSON.stringify(body) : undefined,
  });

  const newAccessToken = response.headers.get("Authorization");

  if (newAccessToken && newAccessToken.startsWith("Bearer ")) {
      const token = newAccessToken.substring(7);
      setTokens({ accessToken: token }); 
  }

  if(response.status === 401 && withAuth) {
    if (!usingRefresh && refreshToken) {
      setTokens({ accessToken: null}); 
      delete finalHeaders["Authorization"]; 
      finalHeaders["Authorization-Refresh-Token"] = `Bearer ${refreshToken}`;
      response = await fetchFn(API_BASE_URL + "/" + url, {
        method,
        headers: finalHeaders,
        body: isFormData ? body : body ? JSON.stringify(body) : undefined,
      });

      const newAccessToken = response.headers.get("Authorization");

      if (newAccessToken && newAccessToken.startsWith("Bearer ")) {
        const token = newAccessToken.substring(7);
        setTokens({ accessToken: token }); 
      }
      
      if(response.status === 401) {
        removeTokens();
        window.location.href = `${base}/login`;
        const err: FetchError = new Error(`Request failed: ${response.status}`);
        err.status = response.status;
        throw err;
      }
    } else {
      removeTokens();
      window.location.href = `${base}/login`;
      throw new Error("Unauthorized");
    }
  }
  

  if (binary) return response.blob() as any;

  const text = await response.text();
  let parsed: TResponse | null = null;
  if (text) {
    try {
      parsed = JSON.parse(text) as TResponse;
    } catch {
      parsed = null;
    }
  }

  return { headers: response.headers, body: parsed } as TResponse;
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