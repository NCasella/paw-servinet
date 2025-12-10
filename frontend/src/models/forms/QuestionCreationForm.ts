import { z } from "zod";

const MAX_LEN = 255;

export type QuestionFormErrorKey =
    | "NotNull.questionForm.serviceId"
    | "NotEmpty.questionForm.serviceId"
    | "NotNull.questionForm.question"
    | "NotEmpty.questionForm.question"
    | "Size.questionForm.question";

export const QuestionFormSchema = z.object({
    /** DTO: serviceId
     *  @NotEmpty
     *  @NotNull
     */
    serviceId: z.coerce
        .number({
            required_error: "NotNull.questionForm.serviceId",
            invalid_type_error: "NotEmpty.questionForm.serviceId",
        })
        .int()
        .positive(),

    /** DTO: question
     *  @NotEmpty
     *  @NotNull
     *  @Size(max=255)
     */
    question: z.coerce
        .string({
            required_error: "NotNull.questionForm.question",
            invalid_type_error: "NotEmpty.questionForm.question",
        })
        .trim()
        .min(1, { message: "NotEmpty.questionForm.question" })
        .max(MAX_LEN, { message: "Size.questionForm.question" }),
});

export type QuestionFormData = z.infer<typeof QuestionFormSchema>;

export type QuestionFormErrors = {
    [K in keyof QuestionFormData]?: QuestionFormErrorKey | string;
};

export class QuestionForm implements QuestionFormData {
    serviceId = 0;
    question = "";

    constructor(init?: Partial<QuestionFormData>) {
        Object.assign(this, init);
    }

    validateQuestionForm(): QuestionFormErrors {
        const result = QuestionFormSchema.safeParse(this);
        const errors: QuestionFormErrors = {};

        if (!result.success) {
            for (const issue of result.error.issues) {
                const field = issue.path[0] as keyof QuestionFormData;

                if (!errors[field]) {
                    errors[field] = issue.message as QuestionFormErrorKey;
                }
            }
        }

        return errors;
    }
}
