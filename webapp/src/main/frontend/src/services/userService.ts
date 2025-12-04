import { User } from "$models/User"
import { getUser, login, logout } from "$stores/userStore"
import { GET } from "$utils/apiFetch"
import { extractUserIdFromToken, removeTokens } from "./authenticate";

export async function getUserInfo(id: number ) :Promise<User> {
    const data = await GET(`users/${id}`); // ya es un objeto
    return User.fromJson(data)
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
    return currentUser;
}

export function closeSession() {
    logout()
    removeTokens()
}