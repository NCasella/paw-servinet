import { z } from "zod";

const MAX_LEN = 255;

const EMAIL_REGEX =
    /^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$/;

const TELEPHONE_REGEX =
    /^\+(\d{1,3})?\s?9?\s?(\d{1,4})?\s?(\d{6,8})$/;


export class UserUpdateForm implements UserUpdateFormData {
    username = "";
    email = "";
    telephone = "";
    locale = "es";
    password = "";

    constructor(init?: Partial<UserUpdateFormData>) {
        Object.assign(this, init);
    }

    validate(): UserUpdateFormErrors {
        const result = UserUpdateFormSchema.safeParse(this);
        const errors: UserUpdateFormErrors = {};

        if (!result.success) {
            for (const issue of result.error.issues) {
                const field = issue.path[0] as keyof UserUpdateFormData;
                if (!errors[field]) {
                    errors[field] = issue.message;
                }
            }
        }

        return errors;
    }
}


export const UserUpdateFormSchema = z.object({
    username: z
        .string()
        .min(1, { message: "NotEmpty.registerUserForm.username" })
        .max(MAX_LEN, { message: "Size.user.username" }),

    email: z
        .string()
        .min(1, { message: "NotEmpty.registerUserForm.email" })
        .max(MAX_LEN, { message: "Size.registerUserForm.email" })
        .regex(EMAIL_REGEX, { message: "Email.registerUserForm.email" }),

    telephone: z
        .string()
        .min(1, { message: "NotEmpty.registerUserForm.telephone" })
        .max(MAX_LEN, { message: "Size.registerUserForm.telephone" })
        .regex(TELEPHONE_REGEX, { message: "Pattern.registerUserForm.telephone" }),

    locale: z
        .string()
        .regex(/^(en|es)$/, { message: "Pattern.registerUserForm.locale" }),

    password: z
        .string()
});


export type UserUpdateFormData = z.infer<typeof UserUpdateFormSchema>;

export type UserUpdateFormErrors = {
    [K in keyof UserUpdateFormData]?: string;
};
