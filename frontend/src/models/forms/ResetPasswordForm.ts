export class ResetPasswordForm {
    email: string = '';
    code: string = '';
    password: string = '';
    confirmPassword: string = '';

    constructor(email: string, code: string, password: string, confirmPassword: string) {
        this.email = email;
        this.code = code;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    validateResetPasswordForm(): { [key: string]: string } {
        const result = ResetPasswordFormSchema.safeParse(this);
        const errors: { [key: string]: string } = {};
        if (!result.success) {
            for (const issue of result.error.issues) {
                const field = issue.path[0] as keyof ResetPasswordForm;
                if (!errors[field]) {
                    //console.log("m"+issue.message)
                    errors[field] = issue.message; 
                }
            }
        }

        return errors;
    }

}

import { z } from "zod";

export type ResetPasswordFormData = z.infer<typeof ResetPasswordFormSchema>;
export type ResetPasswordFormErrors= {
  [K in keyof ResetPasswordFormData]?: string;
};

const MAX_LEN = 255;
export const ResetPasswordFormSchema = z.object({
    email: z
    .string()
    .min(1, { message: "NotEmpty.PasswordResetForm.email" })
    .max(MAX_LEN, { message: "Size.PasswordResetForm.email" })
    .email({ message: "Email.PasswordResetForm.email" }),
  password: z
    .string()
    .trim()
    .min(8, { message: "NotEmpty.PasswordResetForm.password" })
    .max(MAX_LEN, { message: "Size.PasswordResetForm.password" })
    ,
  confirmPassword: z
    .string()
    .trim()
    .nonempty({ message: "NotEmpty.PasswordResetForm.passwordConfirmation" })
    .min(8, { message: "Size.PasswordResetForm.passwordConfirmation" })
    .max(MAX_LEN, { message: "Size.PasswordResetForm.passwordConfirmation" })
    })
    .refine((data) => data.password === data.confirmPassword, {
    message: "FieldsValueMatch.registerUserForm.password",
    path: ["confirmPassword"], 
});
