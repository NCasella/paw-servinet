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
  | "Size.serviceForm.location"
  | "Size.serviceForm.price"
  | "Positive.serviceForm.minimalduration";

export const ServiceFormSchema = z.object({
  /** DTO: serviceName
   *  @Size(max=255) @NotEmpty @NotNull
   *  Mensajes:
   *    - Size.serviceForm.title
   *    - NotEmpty.serviceForm.title
   *    - NotNull.serviceForm.title
   */
  serviceName: z
    .string()
    .trim()
    .min(1, { message: "NotEmpty.serviceForm.title" })
    .max(MAX_LEN, { message: "Size.serviceForm.title" }),
  /** DTO: description
   *  @Size(max=255)
   *  Mensaje: Size.serviceForm.description
   */
  description: z
    .string()
    .max(MAX_LEN, { message: "Size.serviceForm.description" })
    .optional()
    .or(z.literal("")),

  /** DTO: homeService
   *  @NotNull
   *  Mensaje: NotNull.serviceForm.homeserv
   */
  homeService: z
    .boolean()
    .refine((v) => v === true || v === false, {
      message: "NotNull.serviceForm.homeserv",
    }),
    
  /** DTO: neighbourhoods (sin constraints de Bean Validation) */
  neighbourhoods: z.array(z.string()).optional(),

  /** DTO: address
   *  @Size(max=255)
   *  Mensaje: Size.serviceForm.location
   */
  address: z
    .string()
    .max(MAX_LEN, { message: "Size.serviceForm.location" })
    .optional()
    .or(z.literal("")),

  /** DTO: price
   *  @Size(max=10)
   *  @Pattern(regexp = "[0-9]+(\\.[0-9]{1,2})?$")
   *  Mensaje: Size.serviceForm.price (lo usamos para longitud/patrón)
   */
  price: z
    .string()
    .max(MAX_PRICE_LEN, { message: "Size.serviceForm.price" })
    .regex(PRICE_REGEX, { message: "Size.serviceForm.price" })
    .optional(),

  /** DTO: additionalCharges (sin constraints) */
  additionalCharges: z.boolean().optional(),

  /** DTO: pricingType
   *  @NotNull (no me pasaste key específica, uso mensaje default)
   */
  pricingType: z.string().min(1),

  /** DTO: category
   *  @NotNull
   */
  category: z.string().min(1),

  /** DTO: minimalDuration
   *  @Positive
   *  Mensaje: Positive.serviceForm.minimalduration
   */
  minimalDuration: z
    .number()
    .int()
    .positive({ message: "Positive.serviceForm.minimalduration" }),

  /** DTO: imageId
   *  @Positive
   *  (no me pasaste key, uso default)
   */
  imageId: z.number().int().positive().optional(),

  /** DTO: businessId
   *  @NotNull (lo suele rellenar el front a partir del negocio actual)
   */
  businessId: z.number().int().positive().optional(),
});

export type ServiceFormData = z.infer<typeof ServiceFormSchema>;
export type ServiceFormErrors = {
  [K in keyof ServiceFormData]?: ServiceFormErrorKey | string;
};
