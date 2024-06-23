package ar.edu.itba.paw.model;

public enum PricingTypes {
    PER_HOUR ("Por hora", "pricing.perhour"),
    PER_TOTAL ("Total", "pricing.total"),
    BUDGET ("Producto", "pricing.budget"),
    TBD ("A determinar", "pricing.tbd");
    private final String value;
    private final String codeMsg;

   PricingTypes(String value, String codeMsg) {
       this.value = value;
       this.codeMsg = codeMsg;
   }
    public String getValue() {
        return value;
    }

    public String getCodeMsg() {
        return codeMsg;
    }

    public static PricingTypes findByValue(String value) {
        for (PricingTypes pricingType: values()) {
            if (pricingType.getValue().equalsIgnoreCase(value)) {
                return pricingType;
            }
        }
        return null;
    }


}
