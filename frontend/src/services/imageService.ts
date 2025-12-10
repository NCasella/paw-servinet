import { base } from "$app/paths";
import { getNewIdFromPostResponse, POST, GET } from "$utils/apiFetch";

export async function uploadImage(file: File): Promise<number> {
    const formData = new FormData();
    formData.append("image", file);

    const response = await POST<{ headers: Headers; body: any }, FormData>(
        "images",
        formData,
        {
            genericContentType: "multipart/form-data",
        }
    );

    return getNewIdFromPostResponse(response);
}


async function getDefaultAsFile(defaultImageUrl:string): Promise<File> {
    defaultImageUrl = "j" //asset(`/images/default/${defaultImageUrl}.png`)
    const blob = await fetch(defaultImageUrl).then(r => r.blob());
    return new File([blob], "default.png", { type: blob.type });
}

const FALLBACK = `${base}/images/default.jpeg`;
const USER_FALLBACK = `${base}/images/profile_default.png`;

export async function getImage(imageId: number): Promise<string> {
    if (!imageId || imageId <= 0) return FALLBACK;

    try {
        const blob = await GET(`images/${imageId}`, {
            binary: true,
            genericContentType: "multipart/form-data"
        });
        return URL.createObjectURL(blob);

    } catch (e) {
        return FALLBACK;
    }
}

export async function getProfileImage(imageId: number): Promise<string> {
    if (!imageId || imageId <= 0) return USER_FALLBACK;

    try {
        const blob = await GET(`images/${imageId}`, {
            binary: true,
            genericContentType: "multipart/form-data"
        });
        return URL.createObjectURL(blob);

    } catch (e) {
        return USER_FALLBACK;
    }
}
