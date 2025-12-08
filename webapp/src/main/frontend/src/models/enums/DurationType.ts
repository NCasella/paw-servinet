export enum DurationTypes {
  FIFTEEN_MINS = "FIFTEEN_MINS",
  HALF_HOUR = "HALF_HOUR",
  FORTYFIVE_MINS = "FORTYFIVE_MINS",
  HOUR = "HOUR",
  NINETY_MINS = "NINETY_MINS",
  TWO_HOURS = "TWO_HOURS",
  THREE_HOURS = "THREE_HOURS",
}

export const DurationTypesInfo: Record<
  DurationTypes,
  { value: number; codeMsg: string }
> = {
  [DurationTypes.FIFTEEN_MINS]: {
    value: 15,
    codeMsg: "duration.fifteenmins",
  },
  [DurationTypes.HALF_HOUR]: {
    value: 30,
    codeMsg: "duration.halfhour",
  },
  [DurationTypes.FORTYFIVE_MINS]: {
    value: 45,
    codeMsg: "duration.fortyfivemins",
  },
  [DurationTypes.HOUR]: {
    value: 60,
    codeMsg: "duration.hour",
  },
  [DurationTypes.NINETY_MINS]: {
    value: 90,
    codeMsg: "duration.ninetymins",
  },
  [DurationTypes.TWO_HOURS]: {
    value: 120,
    codeMsg: "duration.twohours",
  },
  [DurationTypes.THREE_HOURS]: {
    value: 180,
    codeMsg: "duration.threehours",
  },
};

export const DurationTypesList = [
  DurationTypesInfo.FIFTEEN_MINS,
  DurationTypesInfo.HALF_HOUR,
  DurationTypesInfo.FORTYFIVE_MINS,
  DurationTypesInfo.HOUR,
  DurationTypesInfo.NINETY_MINS,
  DurationTypesInfo.TWO_HOURS,
  DurationTypesInfo.THREE_HOURS,
];
