
export class BusinessForm {
    businessEmail: string;
    businessName: string;
    businessLocation: string;
    businessTelephone: string;

    constructor(businessEmail :string,businessName:string,businessLocation:string,businessTelephone:string) {
        this.businessEmail = businessEmail,
        this.businessLocation = businessLocation,
        this.businessName = businessName,
        this.businessTelephone = businessTelephone
    }

    validateBusinessForm(): BusinessFormErrors {
        const result = BusinessFormSchema.safeParse(this);
        const errors: BusinessFormErrors = {};

        if (!result.success) {
            for (const issue of result.error.issues) {
                const field = issue.path[0] as keyof BusinessFormData;
                // Usamos el primer error por campo
                if (!errors[field]) {
                    console.log("m"+issue.message)
                    errors[field] = issue.message; // ya es la key: "validation.xxx"
                }
            }
        }

        return errors;
    }

}

export type BusinessFormData = z.infer<typeof BusinessFormSchema>;
export type BusinessFormErrors = {
  [K in keyof BusinessFormData]?: string;
};

export type BusinessFormErrorKey =
  | "NotEmpty.BusinessForm.businessName"
  | "Size.BusinessForm.businessName"
  | "NotEmpty.BusinessForm.businessEmail"
  | "Email.BusinessForm.businessEmail"
  | "Size.BusinessForm.businessEmail"
  | "NotEmpty.BusinessForm.businessTelephone"
  | "Pattern.BusinessForm.businessTelephone"
  | "Size.BusinessForm.businessTelephone"
  | "NotEmpty.BusinessForm.businessLocation"
  | "Size.BusinessForm.businessLocation";


import { z } from "zod";

const MAX_LEN = 255;
const PHONE_REGEX = /^\+(\d{1,3})?\s?9?\s?(\d{1,4})?\s?(\d{6,8})$/;

export const BusinessFormSchema = z.object({
  businessName: z
    .string()
    .trim()
    .min(1, { message: "NotEmpty.BusinessForm.businessName" })
    .max(MAX_LEN, { message: "Size.BusinessForm.businessName" }),

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


