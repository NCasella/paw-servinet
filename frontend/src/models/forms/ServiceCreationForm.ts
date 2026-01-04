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
  | "NotEmpty.serviceForm.price"
  | "NotEmpty.serviceForm.pricingType"
  | "NotEmpty.serviceForm.category";

export const ServiceCreateSchema = z
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

    additionalCosts: z.boolean().optional(),

    pricingType: z.string().min(1, { message: "NotEmpty.serviceForm.pricingType" }),

    category: z.string().min(1, { message: "NotEmpty.serviceForm.category" }),

    minimalDuration: z
      .number()
      .int()
      .positive({ message: "Positive.serviceForm.minimalduration" }),

    imageId: z.number().optional(),
    businessId: z.number().optional(),
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

export const ServiceUpdateSchema = z
  .object({
    description: z
      .string()
      .max(MAX_LEN, { message: "Size.serviceForm.description" })
      .optional()
      .or(z.literal("")),

    pricingType: z.string().min(1, { message: "NotEmpty.serviceForm.pricingType" }),

    price: z
      .string()
      .max(MAX_PRICE_LEN, { message: "Size.serviceForm.price" })
      .regex(PRICE_REGEX, { message: "Size.serviceForm.price" })
      .nullable()
      .optional()
      .or(z.literal("")),

    additionalCosts: z.boolean(),

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
  });

export type ServiceFormCreateData = z.infer<typeof ServiceCreateSchema>;
export type ServiceFormUpdateData = z.infer<typeof ServiceUpdateSchema>;

type ServiceFormField = keyof ServiceFormCreateData | keyof ServiceFormUpdateData;

export type ServiceFormErrors = Partial<Record<ServiceFormField, ServiceFormErrorKey | string>>;

function validateWithSchema<T extends z.ZodTypeAny>(schema: T, data: unknown): ServiceFormErrors {
  const result = schema.safeParse(data);
  const errors: ServiceFormErrors = {};

  if (!result.success) {
    for (const issue of result.error.issues) {
      const field = issue.path[0] as ServiceFormField;
      if (!errors[field]) {
        errors[field] = issue.message;
      }
    }
  }

  return errors;
}

export class ServiceForm {
  serviceName = "";
  description = "";
  homeService = false;
  neighbourhoods: string[] = [];
  address = "";
  price: string | null = null;
  additionalCosts = false;
  pricingType = "";
  category = "";
  minimalDuration = 0;
  imageId?: number;
  businessId?: number;

  constructor(init?: Partial<ServiceFormCreateData & ServiceFormUpdateData>) {
    Object.assign(this, init);
  }

  get priceValue(): string | null {
    return this.price;
  }
  set priceValue(val: string) {
    this.price = val === "" ? null : val;
  }

  validateServiceUpdateForm(): ServiceFormErrors {
    return validateWithSchema(ServiceUpdateSchema, this);
  }

  validateServiceCreateForm(): ServiceFormErrors {
    return validateWithSchema(ServiceCreateSchema, this);
  }
}

export type ServiceUpdateForm = {
  description?: string;
  minimalDuration: number;
  pricingType: string;
  price: string | null;
  additionalCosts: boolean;
};
