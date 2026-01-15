// Helper to create TResponse object
export function createResponse(body: any, headers: Headers = new Headers()): { body: any; headers: Headers } {
    return { body, headers };
}