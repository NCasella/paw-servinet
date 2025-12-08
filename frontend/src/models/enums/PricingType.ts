export enum PricingTypes {
  PER_HOUR = "PER_HOUR",
  PER_TOTAL = "PER_TOTAL",
  BUDGET = "BUDGET",
  TBD = "TBD",
}

export const PricingTypesInfo: Record<PricingTypes, { value: string; codeMsg: string }> = {
  [PricingTypes.PER_HOUR]:  { value: "Por hora",    codeMsg: "pricing.perhour" },
  [PricingTypes.PER_TOTAL]: { value: "Total",       codeMsg: "pricing.total" },
  [PricingTypes.BUDGET]:    { value: "Producto",    codeMsg: "pricing.budget" },
  [PricingTypes.TBD]:       { value: "A determinar", codeMsg: "pricing.tbd" },
};


export const PricingTypesList = [ PricingTypesInfo.BUDGET, PricingTypesInfo.PER_HOUR, PricingTypesInfo.PER_TOTAL, PricingTypesInfo.TBD ]