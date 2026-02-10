export enum Neighbourhoods {
  ALMAGRO = "ALMAGRO",
  BALVANERA = "BALVANERA",
  BARRACAS = "BARRACAS",
  BELGRANO = "BELGRANO",
  BOEDO = "BOEDO",
  CABALLITO = "CABALLITO",
  CHACARITA = "CHACARITA",
  COGHLAN = "COGHLAN",
  COLEGIALES = "COLEGIALES",
  CONSTITUCION = "CONSTITUCION",
  FLORES = "FLORES",
  FLORESTA = "FLORESTA",
  LA_BOCA = "LA_BOCA",
  LA_PATERNAL = "LA_PATERNAL",
  LINIERS = "LINIERS",
  MATADEROS = "MATADEROS",
  MONTE_CASTRO = "MONTE_CASTRO",
  MONTSERRAT = "MONTSERRAT",
  NUEVA_POMPEYA = "NUEVA_POMPEYA",
  PALERMO = "PALERMO",
}

export const NeighbourhoodsMap = {
  ALMAGRO: "Almagro",
  BALVANERA: "Balvanera",
  BARRACAS: "Barracas",
  BELGRANO: "Belgrano",
  BOEDO: "Boedo",
  CABALLITO: "Caballito",
  CHACARITA: "Chacarita",
  COGHLAN: "Coghlan",
  COLEGIALES: "Colegiales",
  CONSTITUCION: "Constitución",
  FLORES: "Flores",
  FLORESTA: "Floresta",
  LA_BOCA: "La Boca",
  LA_PATERNAL: "La Paternal",
  LINIERS: "Liniers",
  MATADEROS: "Mataderos",
  MONTE_CASTRO: "Monte Castro",
  MONTSERRAT: "Montserrat",
  NUEVA_POMPEYA: "Nueva Pompeya",
  PALERMO: "Palermo",
} as const;


export type NeighbourhoodCode = keyof typeof NeighbourhoodsMap;
// "ALMAGRO" | "BALVANERA" | ...

export const NeighbourhoodsList = Object.keys(
  NeighbourhoodsMap
) as NeighbourhoodCode[];

export function toNeighbourhoodEnum(value: string): string {
  const v= value.toUpperCase().replace(/ /g, "_");
  //console.log(v)
  return v
}
