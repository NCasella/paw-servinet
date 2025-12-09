import { z } from "zod";

const MAX_LEN = 255;

export type QuestionResponseFormErrorKey =
    | "NotEmpty.questionResponse.response"
    | "NotNull.questionResponse.response"
    | "Size.questionResponse.response";

export const QuestionResponseFormSchema = z.object({
    /** DTO: response
     *  @NotEmpty
     *  @NotNull
     *  @Size(max=255)
     */
    response: z.coerce
        .string({
            required_error: "NotNull.questionResponse.response",
            invalid_type_error: "NotNull.questionResponse.response",
        })
        .trim()
        .min(1, { message: "NotEmpty.questionResponse.response" })
        .max(MAX_LEN, { message: "Size.questionResponse.response" }),
});

export type QuestionResponseFormData = z.infer<typeof QuestionResponseFormSchema>;
export type QuestionResponseFormErrors = {
    [K in keyof QuestionResponseFormData]?: QuestionResponseFormErrorKey | string;
};

export class QuestionResponseForm implements QuestionResponseFormData {
    response = "";

    constructor(init?: Partial<QuestionResponseFormData>) {
        Object.assign(this, init);
    }

    validateQuestionResponseForm(): QuestionResponseFormErrors {
        const result = QuestionResponseFormSchema.safeParse(this);
        const errors: QuestionResponseFormErrors = {};

        if (!result.success) {
            for (const issue of result.error.issues) {
                const field = issue.path[0] as keyof QuestionResponseFormData;

                if (!errors[field]) {
                    errors[field] = issue.message as QuestionResponseFormErrorKey;
                }
            }
        }

        return errors;
    }
}
