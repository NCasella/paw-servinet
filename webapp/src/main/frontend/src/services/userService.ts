import { setLanguage } from "$lib/i18n/i18n";
import { User } from "$models/User"
import { getUser, login, logout } from "$stores/userStore"
import { GET } from "$utils/apiFetch"
import { extractUserIdFromToken, extractUserRolesFromToken, removeTokens } from "./authenticate";

export async function getUserInfo(id: number ) :Promise<User> {
    const data = await GET(`users/${id}`,{contentType:"user-contact-info" });
    
    let user = User.fromJson(data)
    user.setRole( currentUserIsProvider() );
    return user;
}

export async function getCurrentUser() :Promise<User> {
    let currentUser = getUser()
    if ( currentUser )
        return currentUser;

    let id = extractUserIdFromToken();
    if ( id==null ) 
        throw Error("Current user not found: auth is missing");

    currentUser = await getUserInfo(id);
    login(currentUser);
    setLanguage(currentUser.language);

    return currentUser;
}

export function closeSession() {
    logout()
    removeTokens()
}

function currentUserIsProvider() :boolean {
    let roles :string[] = extractUserRolesFromToken()
    return roles?.some((r) => r==="ROLE_BUSINESS")
} 