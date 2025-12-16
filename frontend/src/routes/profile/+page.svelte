<script lang="ts">
    import { t } from "$lib/i18n/i18n";
	import { closeSession, getCurrentUserContactInfo } from "$services/userService";
	import { getCurrentUser } from "$services/userService";
    import {type User } from "$models/User"
    import { onMount } from "svelte";
	import { goto, invalidateAll } from "$app/navigation";
    import { Avatar } from "@skeletonlabs/skeleton-svelte";
	import { UserContactInfo } from "$models/ContactInfo";
	import Spinner from "$lib/components/global/Spinner.svelte";
	import Icon from "$icons";
	import type { PageData } from "./$types";
    
    export let data :PageData
    let { user, otherLang } = data

  function editProfile() {}
</script>

  <div class="max-w-xl mx-auto ">
  <section class="rounded-2xl shadow p-8 space-y-5" >
 

    <div class="flex flex-col gap-6 sm:flex-row sm:items-center">
         <!-- Top: avatar + identity + actions -->
        <Avatar class="h-30 w-30">
            <Avatar.Image src={user.profilePicture} alt="pic" />
            <Avatar.Fallback><img class="rounded-2xl" src={UserContactInfo.getFallbackImage()} alt="pic"/></Avatar.Fallback>
        </Avatar>
      <div class="flex-1 min-w-0">
        <div class="">
            <h2 class="text-2xl font-semibold leading-tight truncate">{user.fullName}</h2>
           <div class="flex text-lg text-primary-600 items-center align-middle gap-1 mt-3">
              <Icon name="person" />
              <span>{user.username}</span>
            </div>
        </div>

        <!-- Datos -->
        <div class="mt-4 space-y-2 text-sm">
          <div class="flex items-center gap-2 opacity-90">
            <Icon name="mail"/>
            <span class="truncate">{user.email}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Preferences -->
    <div class="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2">
      <!-- Idioma -->
      <div class="rounded-xl border p-4">
        <div class="flex items-center gap-2 mb-3">
          <Icon name="language"/>
          <h3 class="font-semibold">{$t('profile.favourite-lang')}</h3>
        </div>

        <form method="post" 
        on:submit|preventDefault={editProfile}
        class="flex items-center gap-3">
          <select
            name="locale"
            class="select select-sm flex-1"
            value={user.language}
          >
            <option value="es">ES</option>
            <option value="en">EN</option>
          </select>

          <button type="submit" class="btn btn-sm bg-primary-400 text-surface-100">
            {$t("service.save-changes")}
          </button>
        </form>
      </div>

      
    </div>

  </section>
</div>


