import { z } from "zod";

const MAX_LEN = 255;
const MAX_PRICE_LEN = 10;
const PRICE_REGEX = /^[0-9]+(\.[0-9]{1,2})?$/;

export class ServiceUpdateForm implements ServiceUpdateFormData {
    description = "";
    pricingType = "";
    price: string | null = null;
    additionalCharges = false;
    minimalDuration = 0;

    constructor(init?: Partial<ServiceUpdateFormData>) {
        Object.assign(this, init);
    }

    get priceValue(): string | null {
        return this.price;
    }

    set priceValue(val: string) {
        this.price = val === "" ? null : val;
    }

    validate(): ServiceUpdateFormErrors {
        const result = ServiceUpdateFormSchema.safeParse(this);
        const errors: ServiceUpdateFormErrors = {};

        if (!result.success) {
            for (const issue of result.error.issues) {
                const field = issue.path[0] as keyof ServiceUpdateFormData;
                if (!errors[field]) {
                    errors[field] = issue.message;
                }
            }
        }

        return errors;
    }
}

export const ServiceUpdateFormSchema = z
    .object({
        description: z
            .string()
            .max(MAX_LEN, { message: "Size.editServiceForm.description" })
            .optional()
            .or(z.literal("")),

        pricingType: z
            .string()
            .min(1, { message: "NotEmpty.editServiceForm.pricingType" }),

        price: z
            .string()
            .max(MAX_PRICE_LEN, { message: "Size.editServiceForm.price" })
            .regex(PRICE_REGEX, { message: "Regex.editServiceForm.price" })
            .nullable()
            .optional()
            .or(z.literal("")),

        additionalCharges: z.boolean().optional(),

        minimalDuration: z
            .number()
            .int()
            .positive({ message: "Positive.editServiceForm.minimalduration" }),
    })
    .superRefine((data, ctx) => {
        const priceNormalized =
            data.price === "" || data.price === undefined ? null : data.price;

        const isTBD = data.pricingType === "TBD";

        if (!isTBD && priceNormalized === null) {
            ctx.addIssue({
                code: z.ZodIssueCode.custom,
                path: ["price"],
                message: "NotEmpty.editServiceForm.price",
            });
        }

        if (isTBD && priceNormalized !== null) {
            ctx.addIssue({
                code: z.ZodIssueCode.custom,
                path: ["price"],
                message: "Size.editServiceForm.price",
            });
        }
    });

export type ServiceUpdateFormData = z.infer<typeof ServiceUpdateFormSchema>;

export type ServiceUpdateFormErrors = {
    [K in keyof ServiceUpdateFormData]?: string;
};
