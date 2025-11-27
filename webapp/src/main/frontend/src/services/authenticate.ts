import { setTokens, clearTokens } from '$stores/auth';

type LoginResponse = {
  accessToken: string;
  refreshToken?: string;
  expiresIn?: number; // lo vamos a implementar???
};

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

  const data = (await response.json()) as LoginResponse;

  setTokens({
    accessToken: data.accessToken,
    refreshToken: data.refreshToken ?? null
  });

  return true;
}

export function logout() {
  clearTokens();
}
