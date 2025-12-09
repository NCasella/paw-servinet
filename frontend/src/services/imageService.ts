import { asset } from "$app/paths";
import { getNewIdFromPostResponse, POST } from "$utils/apiFetch";

export async function postImage(image:File | null, defaultImageUrl:string) {
    if (image == null) image = await getDefaultAsFile(defaultImageUrl)
    
    const formData = new FormData();
    formData.append('image', image)

    try {
        const response = await POST("images", formData)

        return getNewIdFromPostResponse(response)
    } catch {
        throw new Error("Error uploading picture")
    }
}


async function getDefaultAsFile(defaultImageUrl:string): Promise<File> {
    defaultImageUrl = asset(`/images/default/${defaultImageUrl}.png`)
    const blob = await fetch(defaultImageUrl).then(r => r.blob());
    return new File([blob], "default.png", { type: blob.type });
}
