import { setTokens, clearTokens, auth, getAccessToken, type AuthState } from '$stores/auth';


const BASE_URL = import.meta.env.VITE_API_BASE_URL
//console.log("base:"+BASE_URL)

export async function loginWithBasicAuth(
  username: string,
  password: string
): Promise<boolean> {
  const basic = btoa(`${username}:${password}`);
    
  const response = await fetch(BASE_URL, {
    method: "GET", 
    headers: {
      Authorization: `Basic ${basic}`
    }
  });

  if (!response.ok) {
    console.error("Login failed:", response.status);
    return false;
  }
  
  const tokensData = getTokensFromResponse( response);
  
  setTokens(tokensData);

  return true;
}

function getTokensFromResponse(response: Response): AuthState {
  let access = response.headers.get('Authorization-Access-Token');
  let refresh = response.headers.get('authorization-refresh-token');

  return {
    accessToken: access?.replace('Bearer ', '') || null,
    refreshToken: refresh?.replace('Bearer ', '') || null
  }
}


export function removeTokens() {
  clearTokens();
}

import { jwtDecode } from "jwt-decode";

type TokenPayload = {
  sub: string;
  id: number;
  roles?: string[];
};

export function extractUserIdFromToken(): number | null {
  const token = getAccessToken()
  if ( token==null)
    throw Error("Token not found")
 
  try {
    const payload = jwtDecode<TokenPayload>(token);
    return payload.id ?? null;
  } catch (e) {
    console.error("Token inválido:", e);
    return null;
  }
}

export function extractUserRolesFromToken(): string[] {
  const token = getAccessToken();
  if (!token) throw Error("Token not found");

  try {
    const payload = jwtDecode<TokenPayload>(token);

    if (payload.roles) return payload.roles;

  } catch (e) {
    Error("Invalid Token");
  }
  return [];
}
