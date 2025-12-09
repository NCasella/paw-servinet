import { z } from "zod";

const MAX_LEN = 255;

export type ReviewFormErrorKey =
    | "NotNull.reviewForm.serviceId"
    | "NotEmpty.reviewForm.serviceId"
    | "Min.reviewForm.rating"
    | "Max.reviewForm.rating"
    | "Size.reviewForm.comment";

export const ReviewFormSchema = z.object({
    /** DTO: serviceId
     *  @NotEmpty
     *  @NotNull
     */
    serviceId: z.coerce
        .number({
            required_error: "NotNull.reviewForm.serviceId",
            invalid_type_error: "NotEmpty.reviewForm.serviceId",
        })
        .int()
        .positive(),

    /** DTO: rating
     *  @Min(1)
     *  @Max(5)
     */
    rating: z.coerce
        .number()
        .int()
        .min(1, { message: "Min.reviewForm.rating" })
        .max(5, { message: "Max.reviewForm.rating" }),

    /** DTO: comment
     *  @Size(max=255)
     *  Optional (no @NotEmpty)
     */
    comment: z
        .string()
        .max(MAX_LEN, { message: "Size.reviewForm.comment" })
        .optional()
        .or(z.literal("")),
});

export type ReviewFormData = z.infer<typeof ReviewFormSchema>;

export type ReviewFormErrors = {
    [K in keyof ReviewFormData]?: ReviewFormErrorKey | string;
};

export class ReviewForm implements ReviewFormData {
    serviceId = 0;
    rating = 1;
    comment = "";

    constructor(init?: Partial<ReviewFormData>) {
        Object.assign(this, init);
    }

    validateReviewForm(): ReviewFormErrors {
        const result = ReviewFormSchema.safeParse(this);
        const errors: ReviewFormErrors = {};

        if (!result.success) {
            for (const issue of result.error.issues) {
                const field = issue.path[0] as keyof ReviewFormData;

                if (!errors[field]) {
                    errors[field] = issue.message as ReviewFormErrorKey;
                }
            }
        }

        return errors;
    }
}
