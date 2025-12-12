import { resetLanguage, setLanguage } from "$lib/i18n/i18n";
import { Appointment } from "$models/Appointment";
import { UserContactInfo, type ContactInfo } from "$models/ContactInfo";
import { parsePagedResponse, type PagedResult } from "$models/PagedList";
import { User } from "$models/User"
import { getUser, login, logout } from "$stores/userStore"
import { GET, POST } from "$utils/apiFetch"
import { RegisterUserForm } from "$models/forms/UserCreationForm";
import { getNewIdFromPostResponse } from "$utils/apiFetch";
import { extractUserIdFromToken, extractUserRolesFromToken, loginWithBasicAuth, removeTokens } from "./authenticate";
import type { RequestPasswordRecoveryForm } from "$models/forms/RequestPasswordRecoveryForm";
import type { TResponse } from "$utils/apiFetch";
import type { ResetPasswordForm } from "$models/forms/ResetPasswordForm";

export async function getUserInfo(id: number,fetchFn?: typeof fetch ) :Promise<User> {
    const data = await GET(`users/${id}`,{contentType:"user-info", fetchFn: fetchFn });  
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
export async function getCurrentUser(fetchFn?: typeof fetch) :Promise<User> {
    let currentUser = getUser()
    if ( currentUser )
        return currentUser;

    let id = extractUserIdFromToken();
    if ( id==null ) 
        throw Error("Current user not found: auth is missing");

    currentUser = await getUserInfo(id, fetchFn);
    loadUser(currentUser)

    return currentUser;
}

export async function getCurrentUserContactInfo() :Promise<UserContactInfo> {
    let currentUser = getUser(), userid
    if ( !currentUser ) {
        userid = extractUserIdFromToken();
        if ( userid==null ) 
            throw Error("Current user not found: auth is missing");
    } else userid = currentUser.userId
    
    return await getUserContactInfo(userid)
    

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

export function currentUserIsProvider() :boolean {
    let roles :string[] = extractUserRolesFromToken()
    return roles?.some((r) => r==="ROLE_BUSINESS")
} 

export async function  getUserContactInfo(userId:number) :Promise<UserContactInfo> {
    const response = await GET(`users/${userId}`, 
        {contentType: "user-contact-info"})
        
    return UserContactInfo.fromJson(response)
}

export async function getAppointmentClients(  appointmentList: Appointment[] ): Promise<Map<number, ContactInfo>> {

  const userIds = [...new Set(appointmentList.map(a => a.userId))];

  const users = await Promise.all(
    userIds.map(id => getUserContactInfo(id))
  );

  return new Map(
    users.map((u, i) => [userIds[i], u.toContactInfo()])
  );

}