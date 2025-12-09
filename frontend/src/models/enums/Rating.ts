export enum Ratings {
    REGULAR = 'regular',
    BUENO = 'bueno',
    MUY_BUENO = 'muy_bueno',
    EXCELENTE = 'excelente'
}

export const RatingsInfo: Record<Ratings, { minValue: number; codeMsg: string }> = {
    [Ratings.REGULAR]: { minValue: 2, codeMsg: 'rating.regular' },
    [Ratings.BUENO]: { minValue: 3, codeMsg: 'rating.good' },
    [Ratings.MUY_BUENO]: { minValue: 4, codeMsg: 'rating.verygood' },
    [Ratings.EXCELENTE]: { minValue: 5, codeMsg: 'rating.excellent' },
};