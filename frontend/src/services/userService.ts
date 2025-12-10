import { resetLanguage, setLanguage } from "$lib/i18n/i18n";
import { User } from "$models/User"
import { getUser, login, logout } from "$stores/userStore"
import { GET, POST } from "$utils/apiFetch"
import { RegisterUserForm } from "$models/forms/UserCreationForm";
import { getNewIdFromPostResponse } from "$utils/apiFetch";
import { extractUserIdFromToken, extractUserRolesFromToken, loginWithBasicAuth, removeTokens } from "./authenticate";
import type { RequestPasswordRecoveryForm } from "$models/forms/RequestPasswordRecoveryForm";
import type { TResponse } from "$utils/apiFetch";
import type { ResetPasswordForm } from "$models/forms/ResetPasswordForm";

export async function getUserInfo(id: number ) :Promise<User> {
    const data = await GET(`users/${id}`,{contentType:"user-contact-info" });
    
    let user = User.fromJson(data)
    user.setRole( currentUserIsProvider() );
    return user;
}

export async function createUser(form:RegisterUserForm) :Promise<number> {
    const response = await POST("users",form, {
        contentType: "user-registration" 
    })  
    return getNewIdFromPostResponse(response)
}

export async function requestPasswordRecovery(form:RequestPasswordRecoveryForm) : Promise<TResponse> {
    const response = await POST("users",form, {
        contentType: "user-password-recovery-request" 
    })  
    return response
}

export async function resetPassword(form:ResetPasswordForm) : Promise<Boolean> {
    const response = await loginWithBasicAuth(form.code, form.password); 
    return response
}

/* Retrieves user login data */
export async function getCurrentUser() :Promise<User> {
    let currentUser = getUser()
    if ( currentUser )
        return currentUser;

    let id = extractUserIdFromToken();
    if ( id==null ) 
        throw Error("Current user not found: auth is missing");

    currentUser = await getUserInfo(id);
    loadUser(currentUser)

    return currentUser;
}

function loadUser(currentUser:User) {
    login(currentUser);
    setLanguage(currentUser.language);
}

export function closeSession() {
    logout()
    resetLanguage()
    removeTokens()
}

function currentUserIsProvider() :boolean {
    let roles :string[] = extractUserRolesFromToken()
    return roles?.some((r) => r==="ROLE_BUSINESS")
} 