package ar.edu.itba.paw.model;

import java.util.Arrays;

public enum Neighbourhoods {
    ALMAGRO("Almagro"),
    BALVANERA("Balvanera"),
    BARRACAS("Barracas"),
    BELGRANO("Belgrano"),
    BOEDO("Boedo"),
    CABALLITO("Caballito"),
    CHACARITA("Chacarita"),
    COGHLAN("Coghlan"),
    COLEGIALES("Colegiales"),
    CONSTITUCION("Constitucion"),
    FLORES("Flores"),
    FLORESTA("Floresta"),
    LA_BOCA("La Boca"),
    LA_PATERNAL("La Paternal"),
    LINIERS("Liniers"),
    MATADEROS("Mataderos"),
    MONTE_CASTRO("Monte Castro"),
    MONTSERRAT("Montserrat"),
    NUEVA_POMPEYA("Nueva Pompeya"),
    PALERMO("Palermo"),;
    private final String value;
    Neighbourhoods(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Neighbourhoods fromValue(String value) {
        return Arrays.stream(values())
                .filter(v -> v.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid neighbourhood " + value));
    }

    public static Neighbourhoods fromName(String name) {
        try {
            return Neighbourhoods.valueOf(name.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid neighbourhood " + name);
        }
    }

}
