export enum ReviewFilters {
    DATE_ASC = "date_asc",
    DATE_DESC = "date_desc",
    RATING_ASC = "rating_asc",
    RATING_DESC = "rating_desc",
}

export const ReviewFiltersInfo: Record<ReviewFilters, { codeMsg: string }> = {
    [ReviewFilters.DATE_ASC]:    { codeMsg: "reviews.date-asc" },
    [ReviewFilters.DATE_DESC]:   { codeMsg: "reviews.date-desc" },
    [ReviewFilters.RATING_ASC]:  { codeMsg: "reviews.rating-asc" },
    [ReviewFilters.RATING_DESC]: { codeMsg: "reviews.rating-desc" },
};

