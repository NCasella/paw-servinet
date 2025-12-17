import { resetLanguage, setLanguage } from "$lib/i18n/i18n";
import { Appointment } from "$models/Appointment";
import { UserContactInfo, type ContactInfo } from "$models/ContactInfo";
import { User } from "$models/User"
import { getUser, login, logout, user } from "$stores/userStore"
import {GET, PATCH, POST} from "$utils/apiFetch"
import { RegisterUserForm } from "$models/forms/UserCreationForm";
import { getNewIdFromPostResponse } from "$utils/apiFetch";
import { extractUserIdFromToken, extractUserRolesFromToken, loginWithBasicAuth, removeTokens } from "./authenticate";
import type { RequestPasswordRecoveryForm } from "$models/forms/RequestPasswordRecoveryForm";
import type { TResponse } from "$utils/apiFetch";
import type { ResetPasswordForm } from "$models/forms/ResetPasswordForm";
import {UserUpdateForm} from "$models/forms/UserUpdateForm";
import { uploadImage } from "./imageService";

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
    const response = await loginWithBasicAuth(form.email, form.code); 
    if (response){
        let userId = extractUserIdFromToken()

        let updateForm:UserUpdateForm = new UserUpdateForm({
            username: "",
            email: "", 
            telephone: "",
            locale: "",
            password: form.password});

        let newresponse = await PATCH(`users/${userId}`, updateForm, {
            contentType: "user-update"
        });
        return newresponse;
    }


    return response
}

export function getPatchFormWithModifiedFields(UserContactInfo: UserContactInfo, form: UserUpdateForm): UserUpdateForm {

    const patchForm: UserUpdateForm = new UserUpdateForm({
        username: "",
        email: "",
        telephone: "",
        locale: "",
        password: ""
    });

    if (form.username !== UserContactInfo.username) {
        patchForm.username = form.username;
    }
    if (form.email !== UserContactInfo.email) {
        patchForm.email = form.email;
    }
    if (form.telephone !== UserContactInfo.telephone) {
        patchForm.telephone = form.telephone;
    }
    if (form.locale !== UserContactInfo.language) {
        patchForm.locale = form.locale;
    }
    return patchForm;
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

export async function updateUser(userId: number, form: UserUpdateForm) {
    await PATCH(`users/${userId}`, form, {contentType:"user-update"})
}

export async function editUserProfilePic(userId: number, image: File) {
    const profilePicId = await uploadImage(image);
    console.log(profilePicId)
    await PATCH(`users/${userId}`, {profilePicId}, {contentType: "user-update"})
} 