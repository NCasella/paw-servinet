import type { TResponse } from "$utils/apiFetch";

export type ContactInfo = {
  username?: string;
  fullName?: string;
  email: string ;
};


export class UserContactInfo {
  username: string;
  fullName: string;
  email: string;

  constructor(data: { username: string; fullName: string; email: string }) {
    this.email = data.email;
    this.fullName = data.fullName;
    this.username = data.username;
  }

  static fromJson(response: TResponse): UserContactInfo {
    const obj = response.body;
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
    typeof obj.fullName === "string" &&
    typeof obj.email === "string"
  );

}
