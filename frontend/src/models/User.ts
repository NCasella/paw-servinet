import { asset } from "$app/paths";
import { type TResponse } from "$utils/apiFetch";

export class User {
  userId: number;
  fullName: string;
  username: string;
  email: string;
  language: string;
  profilePicture: string | null;
  isProvider: boolean;
  constructor(data: {
    userId: number;
    fullName: string;
    username: string;
    email: string;
    language: string;
    links?: { profilePic?: string; };
    isProvider: boolean;
  }) {
    this.userId = data.userId;
    this.fullName = data.fullName;
    this.username = data.username;
    this.email = data.email;
    this.language = data.language;
    this.profilePicture = data.links?.profilePic ?? null;
    this.isProvider = false;
  }
  

  static fromJson(response: TResponse): User {
    const obj = response.body
    
    if (isUser(obj)) {
      return new User(obj);
    }
    throw new Error("Invalid User JSON");
  }

  getProfilePicture() :string {
    return this.profilePicture? this.profilePicture
    : "https://t4.ftcdn.net/jpg/02/15/84/43/360_F_215844325_ttX9YiIIyeaR7Ne6EaLLjMAmy4GvPC69.jpg" //`/images/${this.profilePictureId}`;
  }

  setRole(isProvider :boolean) {
    this.isProvider = isProvider
  }

  getProfilePictureSrc() {
      if ( this.profilePicture) return this.profilePicture
      return asset("/images/profile_default.png")
   }
  
   static getFallbackImage() {
      return asset("/images/profile_default.png")
  }
}

function isUser(obj: any): obj is User {
  return (
    obj &&
    typeof obj.userId === "number" &&
    typeof obj.fullName === "string" &&
    typeof obj.username === "string" &&
    typeof obj.language === "string" 
  );
}

//function extractProfileImageURLFromJson(url: String) :number{
//    return new Number (url.split('/').pop());
//}

