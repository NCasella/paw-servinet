package ar.edu.itba.paw.model;

public enum Categories {
    LIMPIEZA("Limpieza", "cleaning_services", "category.cleaning"),
    BELLEZA("Belleza", "diamond", "category.beauty"),
    ARREGLOS_CALIFICADOS("Arreglos Calificados", "handyman", "category.qualified"),
    MASCOTAS("Mascotas", "pets", "category.pets"),
    EXTERIORES("Exteriores", "local_florist", "category.outdoors"),
    EVENTOS_Y_CELEBRACIONES ("Eventos y Celebraciones", "celebration", "category.events"),
    TRANSPORTE ("Transporte", "local_shipping", "category.transport"),
    CONSULTORIA ("Consultoria", "help", "category.consulting"),
    PELUQUERIA ("Peluqueria", "face", "category.hairdressing"),
    SALUD ("Salud", "health_and_safety", "category.health"),;

    private final String value;
    private final String icon;
    private final String codeMsg;

    Categories(String value, String icon, String codeMsg){
        this.value = value;
        this.icon = icon;
        this.codeMsg = codeMsg;
    }

    public String getValue() {
        return value;
    }

    public String getIcon() {
        return icon;
    }
    public String getCodeMsg(){
        return codeMsg;
    }

    //TODO: implementar Interfaz que agrupe enums? -> definir findByValue genérico
    public static Categories findByValue(String value) {
        for (Categories category: values()) {
            if (category.getValue().equalsIgnoreCase(value)) {
                return category;
            }
        }
        return null;
    }

    @Override
    public String toString(){
        return value;
    }
}
