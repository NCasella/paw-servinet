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
    icon: "cleaning",
    codeMsg: "category.cleaning",
  },
  [Categories.BELLEZA]: {
    value: "Belleza",
    icon: "beauty",
    codeMsg: "category.beauty",
  },
  [Categories.ARREGLOS_CALIFICADOS]: {
    value: "Arreglos Calificados",
    icon: "repair",
    codeMsg: "category.qualified",
  },
  [Categories.MASCOTAS]: {
    value: "Mascotas",
    icon: "pet",
    codeMsg: "category.pets",
  },
  [Categories.EXTERIORES]: {
    value: "Exteriores",
    icon: "exteriors",
    codeMsg: "category.outdoors",
  },
  [Categories.EVENTOS_Y_CELEBRACIONES]: {
    value: "Eventos y Celebraciones",
    icon: "celebrations",
    codeMsg: "category.events",
  },
  [Categories.TRANSPORTE]: {
    value: "Transporte",
    icon: "transport",
    codeMsg: "category.transport",
  },
  [Categories.CONSULTORIA]: {
    value: "Consultoria",
    icon: "consulting",
    codeMsg: "category.consulting",
  },
  [Categories.PELUQUERIA]: {
    value: "Peluqueria",
    icon: "hairSalon",
    codeMsg: "category.hairdressing",
  },
  [Categories.SALUD]: {
    value: "Salud",
    icon: "health",
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

