import { getPath } from "$lib/navigation/pageInfo";
import type { TResponse } from "$utils/apiFetch";

export type ContactInfo = {
  username?: string;
  fullName?: string;
  email: string ;
};


export class UserContactInfo {
  userId: number;
  username: string;
  fullName: string;
  email: string;
  language: string;
  telephone: string;
  profilePicture: string | null;


  constructor(data: { userId: number, username: string; fullName: string; email: string, language: string, telephone: string, links?: { profilePic?: string; }; }) {
    this.userId = data.userId;
    this.email = data.email;
    this.fullName = data.fullName;
    this.username = data.username;
    this.language = data.language;
    this.telephone = data.telephone;
    this.profilePicture = data.links?.profilePic ?? null;
  }

  static fromJson(response: TResponse): UserContactInfo {
    const obj = response.body;
    //console.log(obj)
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
  

}

function isUserContactInfo(obj: any): obj is UserContactInfo {
  
    return (
    obj &&
    typeof obj.username === "string" &&
    typeof obj.fullName === "string" 
    && typeof obj.email === "string" 
  
  );

}
