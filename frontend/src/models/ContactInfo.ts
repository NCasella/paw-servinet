import { asset } from "$app/paths";
import { getPath } from "$lib/navigation/pageInfo";
import type { TResponse } from "$utils/apiFetch";
import { number } from "zod";

export type ContactInfo = {
  username?: string;
  fullName?: string;
  email: string ;
};


export class UserContactInfo {
  username: string;
  fullName: string;
  email: string;
  language: string;
  profilePicId: number;

  constructor(data: { username: string; fullName: string; email: string, language: string, profilePicId:number }) {
    this.email = data.email;
    this.fullName = data.fullName;
    this.username = data.username;
    this.language = data.language;
    this.profilePicId = data.profilePicId;
  }

  static fromJson(response: TResponse): UserContactInfo {
    const obj = response.body;
    console.log(obj)
    if (isUserContactInfo(obj)) return new UserContactInfo(obj);
    throw new Error("Invalid ContactInfo JSON");
  }

 toContactInfo(): ContactInfo {
    return {
      username: this.username,
      fullName: this.fullName,
      email: this.email,
    } as ContactInfo;
 }
  
 getProfilePictureSrc() {
    if ( this.profilePicId) return getPath(`/images/${this.profilePicId}`)
    return asset("/images/profile_default.png")
 }

 static getFallbackImage() {
    return asset("/images/profile_default.png")
}

}

function isUserContactInfo(obj: any): obj is UserContactInfo {
  return (
    obj &&
    typeof obj.username === "string" &&
    typeof obj.fullName === "string" &&
    typeof obj.email === "string" && 
    typeof obj.profilePicId === "number"
  );

}
