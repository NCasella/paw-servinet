package ar.edu.itba.paw.model;

import java.util.Arrays;

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

    public static PricingTypes fromValue(String value) {
        return Arrays.stream(values())
                .filter(v -> v.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid pricing type: " + value));
    }
}
