export class RequestPasswordRecoveryForm {
    email: string = '';

    constructor(email: string) {
        this.email = email;
    }

    validateRequestPasswordRecoveryForm(): { [key: string]: string } {
        const result = RequestPasswordRecoveryFormSchema.safeParse(this);
        const errors: { [key: string]: string } = {};
        if (!result.success) {
            for (const issue of result.error.issues) {
                const field = issue.path[0] as keyof RequestPasswordRecoveryForm;
                if (!errors[field]) {
                    console.log("m"+issue.message)
                    errors[field] = issue.message; 
                }
            }
        }

        return errors;
    }

}
import { z } from "zod";

export type RequestPasswordRecoveryFormData = z.infer<typeof RequestPasswordRecoveryFormSchema>;
export type RequestPasswordRecoveryFormErrors= {
  [K in keyof RequestPasswordRecoveryFormData]?: string;
};

const MAX_LEN = 255;
export const RequestPasswordRecoveryFormSchema = z.object({
  email: z
    .string()
    .trim()
    .min(1, { message: "NotEmpty.requestPasswordRecoveryForm.email" })
    .max(MAX_LEN, { message: "Size.requestPasswordRecoveryForm.email" })
    .email({ message: "NotEmpty.requestPasswordRecoveryForm.email" }),
});
