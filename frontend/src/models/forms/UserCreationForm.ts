export class RegisterUserForm{
    name: string;
    surname: string;
    email: string;
    telephone: string;
    username: string;
    password: string;
    confirmPassword: string;

    constructor(email :string,name:string,surname:string,telephone:string,username:string,password:string,confirmPassword:string) {
        this.email = email,
        this.name = name,
        this.surname = surname,
        this.telephone = telephone,
        this.username = username,
        this.password = password,
        this.confirmPassword = confirmPassword
    }

    validateRegisterUserForm(): RegisterUserFormErrors {
        const result = RegisterUserFormSchema.safeParse(this);
        const errors: RegisterUserFormErrors = {};

        if (!result.success) {
            for (const issue of result.error.issues) {
                const field = issue.path[0] as keyof RegisterUserFormData;
                if (!errors[field]) {
                    console.log("m"+issue.message)
                    errors[field] = issue.message; 
                }
            }
        }

        return errors;
    }

}

export type RegisterUserFormData = z.infer<typeof RegisterUserFormSchema>;
export type RegisterUserFormErrors= {
  [K in keyof RegisterUserFormData]?: string;
};

export type UserCreationFormErrorKey =
  | "NotEmpty.registerUserForm.name"
  | "Size.registerUserForm.name"
  | "NotEmpty.registerUserForm.surname"
  | "Size.registerUserForm.surname"
  | "NotEmpty.registerUserForm.email"
  | "Email.registerUserForm.email"
  | "Size.registerUserForm.email"
  | "NotEmpty.registerUserForm.telephone"
  | "Pattern.registerUserForm.telephone"
  | "Size.registerUserForm.telephone"
  | "NotEmpty.registerUserForm.username"
  | "Size.registerUserForm.username"
  | "NotEmpty.registerUserForm.password"
  | "Size.registerUserForm.password"
  | "NotEmpty.registerUserForm.passwordConfirmation"
  | "Size.registerUserForm.passwordConfirmation";


import { z } from "zod";

const MAX_LEN = 255;
const PHONE_REGEX = /^\+(\d{1,3})?\s?9?\s?(\d{1,4})?\s?(\d{6,8})$/;

export const RegisterUserFormSchema = z.object({
  name: z
    .string()
    .trim()
    .min(1, { message: "NotEmpty.registerUserForm.name" })
    .max(MAX_LEN, { message: "Size.registerUserForm.name" }),

  surname: z
    .string()
    .trim()
    .min(1, { message: "NotEmpty.registerUserForm.surname" })
    .max(MAX_LEN, { message: "Size.registerUserForm.surname" }),

  email: z
    .string()
    .trim()
    .min(1, { message: "NotEmpty.registerUserForm.email" })
    .max(MAX_LEN, { message: "Size.registerUserForm.email" })
    .email({ message: "Email.registerUserForm.email" }),

  telephone: z
    .string()
    .trim()
    .min(1, { message: "NotEmpty.registerUserForm.telephone" })
    .max(MAX_LEN, { message: "Size.registerUserForm.telephone" })
    .regex(PHONE_REGEX, {
      message: "Pattern.registerUserForm.telephone",
    }),

  username: z
    .string()
    .trim()
    .min(1, { message: "NotEmpty.registerUserForm.username" })
    .max(MAX_LEN, { message: "Size.registerUserForm.username" }),

  password: z
    .string()
    .trim()
    .min(8, { message: "Size.registerUserForm.password" })
    .max(MAX_LEN, { message: "Size.registerUserForm.password" }),

  confirmPassword: z
    .string()
    .trim()
    .min(8, { message: "Size.registerUserForm.confirmPassword" })
    .max(MAX_LEN, { message: "Size.registerUserForm.confirmPassword" })
}).refine((data) => data.password === data.confirmPassword, {
  message: "FieldsValueMatch.registerUserForm.password",
  path: ["confirmPassword"], // Asocia el error con el campo 
});
;