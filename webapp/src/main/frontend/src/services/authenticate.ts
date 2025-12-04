import { setTokens, clearTokens, auth, getAccessToken, type AuthState } from '$stores/auth';


const BASE_URL = import.meta.env.VITE_API_BASE_URL
console.log("base:"+BASE_URL)
console.log
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

  console.log("Response:"+ tokensData.accessToken)
  setTokens(tokensData);

  return true;
}

function getTokensFromResponse(response: Response): AuthState {
  let access = response.headers.get('Authorization-access-token');

    if (access?.startsWith('Bearer ')) {
      access = access.substring('Bearer '.length); // saca el "Bearer "
    }

  return {
    accessToken: access,               
    refreshToken: null // response.headers.get('X-Refresh-Token')
  };
}


export function logout() {
  clearTokens();
}

import { jwtDecode } from "jwt-decode";

type TokenPayload = {
  sub: string;
  id: number;
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