import {GET} from "$utils/apiFetch";
import {base} from "$app/paths";

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
