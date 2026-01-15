<script lang="ts">
  import { t } from "$lib/i18n/i18n";
	import { closeSession, editUserProfilePic, getCurrentUserContactInfo } from "$services/userService";
	import { getCurrentUser } from "$services/userService";
  import {User } from "$models/User"
  import { onMount } from "svelte";
	import { goto, invalidateAll } from "$app/navigation";
  import { Avatar } from "@skeletonlabs/skeleton-svelte";
	import { UserContactInfo } from "$models/ContactInfo";
	import Spinner from "$lib/components/global/Spinner.svelte";
	import Icon from "$icons";
	import type { PageData } from "./$types";
  import {base} from "$app/paths";
	import BigButtonWithOnClick from "$lib/components/global/BigButtonWithOnClick.svelte";
	import BigButtonSecondaryWithOnClick from "$lib/components/global/BigButtonSecondaryWithOnClick.svelte";
    
    export let data :PageData
    let { user } = data

  // ---- modal state
  let showImgModal = false;
  let imgFile: File | null = null;
  let imgPreviewUrl: string | null = null;
  let savingImg = false;
  let imgError: string | null = null;

  function handleEditImg() {
    imgError = null;
    imgFile = null;
    imgPreviewUrl = null;
    showImgModal = true;
  }

  function closeImgModal() {
    showImgModal = false;
    imgError = null;

    // liberar object URL
    if (imgPreviewUrl) URL.revokeObjectURL(imgPreviewUrl);
    imgPreviewUrl = null;
    imgFile = null;
  }

  function onPickImg(e: Event) {
    imgError = null;
    const input = e.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;

    if (!file) {
      imgFile = null;
      if (imgPreviewUrl) URL.revokeObjectURL(imgPreviewUrl);
      imgPreviewUrl = null;
      return;
    }

    // validación rápida (opcional)
    const isOkType = ["image/png", "image/jpeg", "image/jpg", "image/webp"].includes(file.type);
    if (!isOkType) {
      imgError = $t("profile.pic.invalidType") ?? "Formato inválido. Usá PNG/JPG/WEBP.";
      input.value = "";
      return;
    }

    const maxMb = 5;
    if (file.size > maxMb * 1024 * 1024) {
      imgError = $t("profile.pic.tooLarge") ?? `La imagen es muy grande (máx ${maxMb}MB).`;
      input.value = "";
      return;
    }

    imgFile = file;

    if (imgPreviewUrl) URL.revokeObjectURL(imgPreviewUrl);
    imgPreviewUrl = URL.createObjectURL(file);
  }

  async function saveProfilePic() {
    if (!imgFile) {
      imgError = $t("profile.pic.required") ?? "Elegí una imagen primero.";
      return;
    }

    savingImg = true;
    imgError = null;

    try {
      // ✅ La idea: tu editUser se ocupa del PATCH.
      // Ajustá el payload según tu API:
      // - si mandás multipart: editUser({ profilePic: imgFile })
      // - si mandás url/id: primero subís, te devuelve url/id, y después patch
      await editUserProfilePic(user.userId, imgFile);
      window.location.reload()

      // refrescar datos (depende de tu setup)
      await invalidateAll();
      closeImgModal();
    } catch (e) {
      console.error(e);
      imgError = $t("profile.pic.uploadError") ?? "No se pudo actualizar la foto. Probá de nuevo.";
    } finally {
      savingImg = false;
    }
  }

</script>

  <div class="max-w-2xl mx-auto ">
  <section class="rounded-2xl shadow p-8 space-y-5" >
 

    <div class="flex flex-col gap-8 sm:flex-row sm:items-center">
     
      
      <Avatar class="h-40 w-40">
            <Avatar.Image class="w-full h-full" src={user.profilePicture} alt="pic" />
            <Avatar.Fallback><img class="rounded-2xl" src={User.getFallbackImage()} alt="pic" loading="lazy" /></Avatar.Fallback>
        </Avatar>
      <button onclick={handleEditImg} class="bg-surface-400 rounded-3xl p-3 z-1 -ml-16 mt-31" ><Icon name="edit"/></button>     
      <div class="flex-1 min-w-0">
        <div>
            <h2 class="text-2xl font-semibold leading-tight truncate">{user.fullName}</h2>
           <div class="flex text-lg text-primary-600 items-center align-middle gap-1 mt-3">
              <Icon name="person" />
              <span>{user.username}</span>
            </div>
        </div>

        <!-- Datos -->
      <div class="flex items-center gap-2 opacity-90 my-4">
        <Icon name="mail"/>
        <span class="truncate">{user.email}</span>
      </div>

      <div class="flex items-center gap-2 opacity-90">
          <Icon name="language"/>
          <h3 class="font-semibold">{$t('profile.favourite-lang')} <span>{user.language}</span></h3>
      </div>

      <div class="flex items-center gap-2 opacity-90 mt-4">
          <Icon name="phone"/>
          <h3 class="font-semibold">{user.telephone}</h3>
      </div>
      </div>
    </div>

    <div class="flex justify-end gap-3 pt-2">
      <BigButtonSecondaryWithOnClick title={$t("profile.change-password")} onclick={goto(`${base}/profile/change-password`)} />
      <BigButtonWithOnClick title={$t("profile.edit")} onclick={goto(`${base}/profile/edit`)}/>
    </div>
  </section>
</div>


{#if showImgModal}
  <div class="fixed inset-0 z-50">
    <!-- overlay -->
    <button
      type="button"
      class="absolute inset-0 bg-black/40"
      onclick={closeImgModal}
      aria-label="Close"
    />

    <!-- dialog -->
    <div class="relative h-full flex items-center justify-center p-4">
      <div class="w-full max-w-md rounded-2xl shadow bg-white p-6 space-y-5">
        <div class="flex items-start justify-between gap-3">
          <div>
            <h3 class="text-lg font-semibold">
              {$t("profile.pic.edit") ?? "Editar foto de perfil"}
            </h3>
            <p class="text-sm opacity-70">
              {$t("profile.pic.hint") ?? "Subí una imagen nueva y guardá los cambios."}
            </p>
          </div>

          
        </div>

        <!-- preview -->
        <div class="flex items-center gap-4">
          <div class="h-20 w-20 rounded-2xl overflow-hidden border bg-surface-100 flex items-center justify-center">
            {#if imgPreviewUrl}
              <img src={imgPreviewUrl} alt="preview" class="h-full w-full object-cover" loading="lazy" />
            {:else}
              <img src={user.profilePicture || User.getFallbackImage()} alt="current" class="h-full w-full object-cover" loading="lazy" />
            {/if}
          </div>

          <div class="flex-1">
            <label class="block text-sm font-medium mb-1">
              {$t("profile.pic.choose") ?? "Elegir imagen"}
            </label>
            <input
              type="file"
              accept="image/png,image/jpeg,image/jpg,image/webp"
              class="w-full border rounded-xl px-3 py-2 text-sm"
              onchange={onPickImg}
              disabled={savingImg}
            />
            {#if imgError}
              <p class="text-sm mt-2 text-error-500">{imgError}</p>
            {/if}
          </div>
        </div>

        <!-- actions -->
        <div class="flex justify-end gap-2 pt-2">
          <button type="button" class="btn btn-sm bg-surface-100" onclick={closeImgModal} disabled={savingImg}>
            {$t("common.cancel") ?? "Cancelar"}
          </button>

          <button type="button" class="btn btn-sm btn-primary" onclick={saveProfilePic} disabled={savingImg}>
            {savingImg ? ($t("common.saving") ?? "Guardando...") : ($t("common.save") ?? "Guardar")}
          </button>
        </div>
      </div>
    </div>
  </div>
{/if}
