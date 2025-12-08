package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.exceptions.InvalidOperationException;

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
                .orElseThrow(() -> new InvalidOperationException("Invalid pricing type " + value));
    }

    public static PricingTypes fromName(String name) {
        try {
            return PricingTypes.valueOf(name.toUpperCase());
        } catch (Exception e) {
            throw new InvalidOperationException("Invalid pricing type " + name);
        }
    }
}
