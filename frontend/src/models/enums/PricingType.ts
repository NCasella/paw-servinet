export enum PricingTypes {
  PER_HOUR = "PER_HOUR",
  PER_TOTAL = "PER_TOTAL",
  BUDGET = "BUDGET",
  TBD = "TBD",
}

export const PricingTypesInfo: Record<
  PricingTypes,
  { value: PricingTypes; codeMsg: string }
> = {
  [PricingTypes.PER_HOUR]:  { value: PricingTypes.PER_HOUR,  codeMsg: "pricing.perhour" },
  [PricingTypes.PER_TOTAL]: { value: PricingTypes.PER_TOTAL, codeMsg: "pricing.total" },
  [PricingTypes.BUDGET]:    { value: PricingTypes.BUDGET,    codeMsg: "pricing.budget" },
  [PricingTypes.TBD]:       { value: PricingTypes.TBD,       codeMsg: "pricing.tbd" },
};


export const PricingTypesList = [ PricingTypes.BUDGET, PricingTypes.PER_HOUR, PricingTypes.PER_TOTAL, PricingTypes.TBD ]