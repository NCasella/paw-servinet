import { z } from "zod";

const MAX_LEN = 255;
const PHONE_REGEX = /^\+(\d{1,3})?\s?9?\s?(\d{1,4})?\s?(\d{6,8})$/;

export const BusinessUpdateFormSchema = z.object({
    businessEmail: z
        .string()
        .trim()
        .min(1, { message: "NotEmpty.BusinessForm.businessEmail" })
        .max(MAX_LEN, { message: "Size.BusinessForm.businessEmail" })
        .email({ message: "Email.BusinessForm.businessEmail" }),

    businessTelephone: z
        .string()
        .trim()
        .min(1, { message: "NotEmpty.BusinessForm.businessTelephone" })
        .max(MAX_LEN, { message: "Size.BusinessForm.businessTelephone" })
        .regex(PHONE_REGEX, {
            message: "Pattern.BusinessForm.businessTelephone",
        }),

    businessLocation: z
        .string()
        .trim()
        .min(1, { message: "NotEmpty.BusinessForm.businessLocation" })
        .max(MAX_LEN, { message: "Size.BusinessForm.businessLocation" }),
});


export type BusinessUpdateFormData = z.infer<typeof BusinessUpdateFormSchema>;

export type BusinessUpdateFormErrors = {
    [K in keyof BusinessUpdateFormData]?: string;
};

export type BusinessUpdateFormErrorKey =
    | "NotEmpty.BusinessForm.businessEmail"
    | "Email.BusinessForm.businessEmail"
    | "Size.BusinessForm.businessEmail"
    | "NotEmpty.BusinessForm.businessTelephone"
    | "Pattern.BusinessForm.businessTelephone"
    | "Size.BusinessForm.businessTelephone"
    | "NotEmpty.BusinessForm.businessLocation"
    | "Size.BusinessForm.businessLocation";


export class BusinessUpdateForm {
    businessEmail: string;
    businessTelephone: string;
    businessLocation: string;

    constructor(
        businessEmail: string,
        businessTelephone: string,
        businessLocation: string
    ) {
        this.businessEmail = businessEmail;
        this.businessTelephone = businessTelephone;
        this.businessLocation = businessLocation;
    }

    validateBusinessUpdateForm(): BusinessUpdateFormErrors {
        const result = BusinessUpdateFormSchema.safeParse(this);
        const errors: BusinessUpdateFormErrors = {};

        if (!result.success) {
            for (const issue of result.error.issues) {
                const field = issue.path[0] as keyof BusinessUpdateFormData;
                if (!errors[field]) {
                    errors[field] = issue.message;
                }
            }
        }

        return errors;
    }
}
