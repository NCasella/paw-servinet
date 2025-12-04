export class User {
  userId: number;
  fullName: string;
  username: string;
  email: string;
  language: string;

  constructor(data: {
    userId: number;
    fullName: string;
    username: string;
    email: string;
    language: string;
  }) {
    this.userId = data.userId;
    this.fullName = data.fullName;
    this.username = data.username;
    this.email = data.email;
    this.language = data.language;
  }
  

  static fromJson(obj: any): User {
    if (isUser(obj)) {
      return new User(obj);
    }
    throw new Error("Invalid User JSON");
  }

}

function isUser(obj: any): obj is User {
  return (
    obj &&
    typeof obj.userId === "number" &&
    typeof obj.fullName === "string" &&
    typeof obj.username === "string" &&
    typeof obj.email === "string" &&
    typeof obj.language === "string"
  );
}
