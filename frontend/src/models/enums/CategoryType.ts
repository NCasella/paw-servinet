export enum Categories {
  LIMPIEZA = "LIMPIEZA",
  BELLEZA = "BELLEZA",
  ARREGLOS_CALIFICADOS = "ARREGLOS_CALIFICADOS",
  MASCOTAS = "MASCOTAS",
  EXTERIORES = "EXTERIORES",
  EVENTOS_Y_CELEBRACIONES = "EVENTOS_Y_CELEBRACIONES",
  TRANSPORTE = "TRANSPORTE",
  CONSULTORIA = "CONSULTORIA",
  PELUQUERIA = "PELUQUERIA",
  SALUD = "SALUD",
}

export const CategoriesInfo: Record<
  Categories,
  { value: string; icon: string; codeMsg: string }
> = {
  [Categories.LIMPIEZA]: {
    value: "Limpieza",
    icon: "cleaning_services",
    codeMsg: "category.cleaning",
  },
  [Categories.BELLEZA]: {
    value: "Belleza",
    icon: "diamond",
    codeMsg: "category.beauty",
  },
  [Categories.ARREGLOS_CALIFICADOS]: {
    value: "Arreglos Calificados",
    icon: "handyman",
    codeMsg: "category.qualified",
  },
  [Categories.MASCOTAS]: {
    value: "Mascotas",
    icon: "pets",
    codeMsg: "category.pets",
  },
  [Categories.EXTERIORES]: {
    value: "Exteriores",
    icon: "local_florist",
    codeMsg: "category.outdoors",
  },
  [Categories.EVENTOS_Y_CELEBRACIONES]: {
    value: "Eventos y Celebraciones",
    icon: "celebration",
    codeMsg: "category.events",
  },
  [Categories.TRANSPORTE]: {
    value: "Transporte",
    icon: "local_shipping",
    codeMsg: "category.transport",
  },
  [Categories.CONSULTORIA]: {
    value: "Consultoria",
    icon: "help",
    codeMsg: "category.consulting",
  },
  [Categories.PELUQUERIA]: {
    value: "Peluqueria",
    icon: "face",
    codeMsg: "category.hairdressing",
  },
  [Categories.SALUD]: {
    value: "Salud",
    icon: "health_and_safety",
    codeMsg: "category.health",
  },
};

export const CategoriesList = [
  CategoriesInfo.LIMPIEZA,
  CategoriesInfo.BELLEZA,
  CategoriesInfo.ARREGLOS_CALIFICADOS,
  CategoriesInfo.MASCOTAS,
  CategoriesInfo.EXTERIORES,
  CategoriesInfo.EVENTOS_Y_CELEBRACIONES,
  CategoriesInfo.TRANSPORTE,
  CategoriesInfo.CONSULTORIA,
  CategoriesInfo.PELUQUERIA,
  CategoriesInfo.SALUD,
];

export const CategoriesTypeList = [
  Categories.LIMPIEZA,
  Categories.BELLEZA,
  Categories.ARREGLOS_CALIFICADOS,
  Categories.MASCOTAS,
  Categories.EXTERIORES,
  Categories.EVENTOS_Y_CELEBRACIONES,
  Categories.TRANSPORTE,
  Categories.CONSULTORIA,
  Categories.PELUQUERIA,
  Categories.SALUD,
];

