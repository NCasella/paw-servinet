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
    import BigButtonSecondary from "$lib/components/global/BigButtonSecondary.svelte";
    import {base} from "$app/paths";
    
    export let data :PageData
    let { user } = data

</script>

  <div class="max-w-xl mx-auto ">
  <section class="rounded-2xl shadow p-8 space-y-5" >
 

    <div class="flex flex-col gap-6 sm:flex-row sm:items-center">
        <Avatar class="h-40 w-40">
            <Avatar.Image src={user.profilePicture} alt="pic" />
            <Avatar.Fallback><img class="rounded-2xl" src={UserContactInfo.getFallbackImage()} alt="pic"/></Avatar.Fallback>
        </Avatar>
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

    <div class="flex w-full justify-end mt-10" on:click={goto(`${base}/profile/edit`)}>
      <BigButtonSecondary title={$t("profile.edit")} />
    </div>
  </section>
</div>


