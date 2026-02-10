export class UserPasswordUpdateForm{
    newPassword: string;
    confirmNewPassword: string;

    constructor(newPassword :string,confirmNewPassword :string) {
        this.newPassword = newPassword,
        this.confirmNewPassword = confirmNewPassword
    }

    validate(): UserPasswordUpdateFormErrors {
        const result = UserPasswordUpdateFormSchema.safeParse(this);
        const errors: UserPasswordUpdateFormErrors = {};

        if (!result.success) {
            for (const issue of result.error.issues) {
                const field = issue.path[0] as keyof UserPasswordUpdateFormData;
                if (!errors[field]) {
                    //console.log("m"+issue.message)
                    errors[field] = issue.message; 
                }
            }
        }

        return errors;
    }

}

export type UserPasswordUpdateFormData = z.infer<typeof UserPasswordUpdateFormSchema>;
export type UserPasswordUpdateFormErrors = {
  [K in keyof UserPasswordUpdateFormData]?: string;
};

export type UserPasswordFormErrorKey =
  | "NotEmpty.registerUserForm.password"
  | "Size.registerUserForm.password"
  | "NotEmpty.registerUserForm.confirmPassword"
  | "Size.registerUserForm.confirmPassword"
  | "FieldsValueMatch.registerUserForm.password";

import { z } from "zod";
const MAX_LEN = 255;

export const UserPasswordUpdateFormSchema = z.object({
    newPassword: z
    .string()
    .trim()
    .min(8, { message: "Size.registerUserForm.password" })
    .max(MAX_LEN, { message: "Size.registerUserForm.password" }),

    confirmNewPassword: z
    .string()
    .trim()
    .min(8, { message: "Size.registerUserForm.confirmPassword" })
    .max(MAX_LEN, { message: "Size.registerUserForm.confirmPassword" })
}).refine((data) => data.newPassword === data.confirmNewPassword, {
  message: "FieldsValueMatch.registerUserForm.password",
  path: ["confirmNewPassword"], 
});
