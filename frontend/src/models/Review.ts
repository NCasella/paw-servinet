import type { TResponse } from "$utils/apiFetch";

export interface ReviewLinks {
    user: string;
    service: string;
    self: string;
}

export class Review {
    ratingIdd: number;
    serviceId: number;
    userId: number;
    rating: number;
    comment: string;
    date: string;
    links: ReviewLinks;

    constructor(data: {
        ratingIdd: number;
        serviceId: number;
        userId: number;
        rating: number;
        comment: string;
        date: string;
        links: ReviewLinks;
    }) {
        this.ratingIdd = data.ratingIdd;
        this.serviceId = data.serviceId;
        this.userId = data.userId;
        this.rating = data.rating;
        this.comment = data.comment;
        this.date = data.date;
        this.links = data.links;
    }

    static fromJson(response: TResponse): Review {
        const obj = response.body;
        if (isReview(obj)) {
            return new Review(obj);
        }
        throw new Error("Invalid Review JSON");
    }
}

function isReview(obj: any): obj is Review {
    return (
        obj &&
        typeof obj.ratingIdd === "number" &&
        typeof obj.serviceId === "number" &&
        typeof obj.userId === "number" &&
        typeof obj.rating === "number" &&
        typeof obj.comment === "string" &&
        typeof obj.date === "string" &&
        obj.links &&
        typeof obj.links.user === "string" &&
        typeof obj.links.service === "string" &&
        typeof obj.links.self === "string"
    );
}
