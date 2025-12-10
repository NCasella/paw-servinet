import type { TResponse } from "$utils/apiFetch";

export interface BusinessUpdateInfo {
  businessEmail: string,
  businessTelephone: string,
  businessLocation: string
}

export class Business {
    businessId :number;
    userId: number;
    businessName :string;
    email :string;
    telephone :string;
    address :string;
    rating :number;

    constructor( data: { businessId :number, userId: number, businessName :string, email :string, telephone: string, address:string , rating :number}) {
        this.businessId =  data.businessId
        this.userId = data.userId
        this.businessName = data.businessName
        this.email = data.email
        this.telephone = data.telephone
        this.address = data.address
        this.rating = data.rating
    }

    static fromJson(response: TResponse): Business {
        console.log( JSON.stringify(response))
        const obj = response.body
        console.log( obj)
        if (isBusiness(obj)) {
          return new Business(obj);
        }
        throw new Error("Invalid Business JSON");
      }
}

function isBusiness(obj: any): obj is Business {
  
    return (
    obj &&
    typeof obj.businessId === "number" &&
    typeof obj.userId === "number" &&
    typeof obj.businessName === "string" &&
    typeof obj.telephone === "string" &&
    typeof obj.email === "string" &&
    typeof obj.address === "string" &&
    typeof obj.rating === "number"
  );
}