import type { TResponse } from "$utils/apiFetch";

export class Service {
  additionalCosts: boolean;
  address: string;
  businessId: number;
  category: string;
  description: string;
  duration: number;
  homeService: boolean;
  links: {
    business: string;
    questions: string;
    reviews: string;
    self: string;
  };
  neighbourhoods: string[];
  price: string;
  pricingType: string;
  rating: number;
  serviceId: number;
  serviceName: string;

  constructor(data: {
    additionalCosts: boolean;
    address: string;
    businessId: number;
    category: string;
    description: string;
    duration: number;
    homeService: boolean;
    links: {
      business: string;
      questions: string;
      reviews: string;
      self: string;
    };
    neighbourhoods: string[];
    price: string;
    pricingType: string;
    rating: number;
    serviceId: number;
    serviceName: string;
  }) {
    this.additionalCosts = data.additionalCosts;
    this.address = data.address;
    this.businessId = data.businessId;
    this.category = data.category;
    this.description = data.description;
    this.duration = data.duration;
    this.homeService = data.homeService;
    this.links = data.links;
    this.neighbourhoods = data.neighbourhoods;
    this.price = data.price;
    this.pricingType = data.pricingType;
    this.rating = data.rating;
    this.serviceId = data.serviceId;
    this.serviceName = data.serviceName;
  }

  static fromJson(response: TResponse): Service {
    const obj = response.body;
    if (isService(obj)) {
      return new Service(obj);
    }
    throw new Error("Invalid Service JSON");
  }
}

function isService(obj: any): obj is Service {
  return (
    obj &&
    typeof obj.additionalCosts === "boolean" &&
    typeof obj.address === "string" &&
    typeof obj.businessId === "number" &&
    typeof obj.category === "string" &&
    typeof obj.description === "string" &&
    typeof obj.duration === "number" &&
    typeof obj.homeService === "boolean" &&
    obj.links &&
    typeof obj.links.business === "string" &&
    typeof obj.links.questions === "string" &&
    typeof obj.links.reviews === "string" &&
    typeof obj.links.self === "string" &&
    Array.isArray(obj.neighbourhoods) &&
    typeof obj.price === "string" &&
    typeof obj.pricingType === "string" &&
    typeof obj.rating === "number" &&
    typeof obj.serviceId === "number" &&
    typeof obj.serviceName === "string"
  );
}
