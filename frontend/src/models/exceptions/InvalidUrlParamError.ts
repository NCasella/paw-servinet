export class InvalidUrlParamError extends Error {
    
    constructor(message: string) {
        super(message);
        this.name = "InvalidUrlParamError";
        Object.setPrototypeOf(this, InvalidUrlParamError.prototype);
    }
}