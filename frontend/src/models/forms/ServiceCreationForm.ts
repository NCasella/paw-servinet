export class ServiceForm implements ServiceFormData {
  serviceName = "";
  description = "";
  homeService = false;
  neighbourhoods: string[] = [];
  address = "";
  price: string | null = null;
  additionalCharges = false;
  pricingType = "";
  category = "";
  minimalDuration = 0;
  imageId?: number;
  businessId?: number;

  constructor(init?: Partial<ServiceFormData>) {
    Object.assign(this, init);
  }

  get priceValue(): string | null {
    return this.price;
  }
  set priceValue(val: string) {
    this.price = val === "" ? null : val;
  }

  validateServiceForm(): ServiceFormErrors {
    const result = ServiceFormSchema.safeParse(this);
    const errors: ServiceFormErrors = {};

    if (!result.success) {
      for (const issue of result.error.issues) {
        const field = issue.path[0] as keyof ServiceFormData;

        if (!errors[field]) {
          errors[field] = issue.message as ServiceFormErrorKey;
        }
      }
    }

    return errors;
  }
}

import { z } from "zod";

const MAX_LEN = 255;
const MAX_PRICE_LEN = 10;
const PRICE_REGEX = /^[0-9]+(\.[0-9]{1,2})?$/;

export type ServiceFormErrorKey =
  | "Size.serviceForm.title"
  | "NotEmpty.serviceForm.title"
  | "NotNull.serviceForm.title"
  | "Size.serviceForm.description"
  | "NotNull.serviceForm.homeserv"
  | "Size.appointmentForm.location"
  | "Size.serviceForm.price"
  | "Positive.serviceForm.minimalduration"
  | "NotEmpty.serviceForm.neighbourhoods"
  | "NotEmpty.serviceForm.price"; 

export const ServiceFormSchema = z
  .object({
    serviceName: z
      .string()
      .trim()
      .min(1, { message: "NotEmpty.serviceForm.title" })
      .max(MAX_LEN, { message: "Size.serviceForm.title" }),

    description: z
      .string()
      .max(MAX_LEN, { message: "Size.serviceForm.description" })
      .optional()
      .or(z.literal("")),

    homeService: z.boolean().refine((v) => v === true || v === false, {
      message: "NotNull.serviceForm.homeserv",
    }),

    neighbourhoods: z
      .array(z.string())
      .min(1, { message: "NotEmpty.serviceForm.neighbourhoods" }),

    address: z
      .string()
      .max(MAX_LEN, { message: "Size.appointmentForm.location" })
      .optional()
      .or(z.literal("")),

    price: z
      .string()
      .max(MAX_PRICE_LEN, { message: "Size.serviceForm.price" })
      .regex(PRICE_REGEX, { message: "Size.serviceForm.price" })
      .nullable()
      .optional()
      .or(z.literal("")),

    additionalCharges: z.boolean().optional(),

    pricingType: z.string().min(1, {message: "NotEmpty.serviceForm.pricingType"}),

    category: z.string().min(1 , {message: "NotEmpty.serviceForm.category"}),

    minimalDuration: z
      .number()
      .int()
      .positive({ message: "Positive.serviceForm.minimalduration" }),
  })
  .superRefine((data, ctx) => {
    const priceNormalized =
      data.price === "" || data.price === undefined ? null : data.price;

    const isTBD = data.pricingType === "TBD";
    if (!isTBD && priceNormalized === null) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ["price"],
        message: "NotEmpty.serviceForm.price",
      });
    }
    if (isTBD && priceNormalized !== null) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ["price"],
        message: "Size.serviceForm.price",
      });
    }

    if (data.homeService === false) {
      const addr = (data.address ?? "").trim();
      if (!addr) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          path: ["address"],
          message: "NotEmpty.appointmentForm.location",
        });
      }
    }
  });

export type ServiceFormData = z.infer<typeof ServiceFormSchema>;
export type ServiceFormErrors = {
  [K in keyof ServiceFormData]?: ServiceFormErrorKey | string;
};
