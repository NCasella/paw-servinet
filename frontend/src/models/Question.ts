import type { TResponse } from "$utils/apiFetch";

export interface QuestionLinks {
    self: string;
    service: string;
    user: string;
}

export class Question {
    questionId: number;
    serviceId: number;
    userId: number;
    question: string;
    response: string | null;
    date: string;
    links: QuestionLinks;

    constructor(data: {
        questionId: number;
        serviceId: number;
        userId: number;
        question: string;
        response: string | null;
        date: string;
        links: QuestionLinks;
    }) {
        this.questionId = data.questionId;
        this.serviceId = data.serviceId;
        this.userId = data.userId;
        this.question = data.question;
        this.response = data.response;
        this.date = data.date;
        this.links = data.links;
    }

    static fromJson(response: TResponse): Question {
        const obj = response.body;
        if (isQuestion(obj)) {
            return new Question(obj);
        }
        throw new Error("Invalid Question JSON");
    }
}

function isQuestion(obj: any): obj is Question {
    return (
        obj &&
        typeof obj.questionId === "number" &&
        typeof obj.serviceId === "number" &&
        typeof obj.userId === "number" &&
        typeof obj.question === "string" &&
        (typeof obj.response === "string" || obj.response === null) &&
        typeof obj.date === "string" &&
        obj.links &&
        typeof obj.links.self === "string" &&
        typeof obj.links.service === "string" &&
        typeof obj.links.user === "string"
    );
}
