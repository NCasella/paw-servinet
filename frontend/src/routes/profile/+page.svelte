<script lang="ts">
    import { t } from "$lib/i18n/i18n";
	import { closeSession, getCurrentUserContactInfo } from "$services/userService";
	import { getCurrentUser } from "$services/userService";
    import {type User } from "$models/User"
    import { onMount } from "svelte";
	import { goto } from "$app/navigation";
    import { Avatar } from "@skeletonlabs/skeleton-svelte";
	import { UserContactInfo } from "$models/ContactInfo";
	import Spinner from "$lib/components/global/Spinner.svelte";

    let user :User;
    let otherLang :string;
    let loading = true
    onMount(async () => {
		try {
		  user = await getCurrentUser()

		otherLang = user.language === 'es' ? 'en' : 'es'
		//} catch {
		  //goto("login")
    } finally {
      loading = false
    }
  })
  
    function logUserOut () {
        closeSession()
        goto("login") 
    }

</script>

{#if loading}
<Spinner/>

{:else if user}
<div class="max-w-5xl mx-auto space-y-8">
  <!-- Perfil -->
  <section class="rounded-2xl shadow p-6 flex items-center gap-6">
    <!-- Avatar -->
    
    <Avatar class="size-30 ">
				<!--<Avatar.Image src="{user.getProfilePictureSrc()}" alt="base" />-->
				<Avatar.Fallback><img class="rounded-2xl" src={UserContactInfo.getFallbackImage()}/></Avatar.Fallback>
    </Avatar>
    <!-- Datos usuario -->
    <div class="flex-1 space-y-3">
      <div>
        <h2 class="text-2xl font-semibold leading-tight">{user.fullName}</h2>
        <p class="text-sm opacity-80">{user.username}</p>
      </div>

      <div class="space-y-1 text-sm">
        <div class="flex items-center gap-2">
          <span class="material-icons text-base">mail</span>
          <span>{user.email}</span>
        </div>
      </div>

      <!-- Idioma preferido -->
      <div class="mt-2 flex flex-wrap items-center gap-3 text-sm">
        <span class="material-icons text-base">language</span>
        <span>{$t('profile.favourite-lang')}:</span>
        <span class="font-medium uppercase">{user.language}</span>

        <!-- “Dropdown” simple con el otro idioma -->
        <details class="inline-block">
          <summary class="list-none cursor-pointer inline-flex items-center gap-1">
            <span class="text-xs uppercase">{otherLang}</span>
            <span class="material-icons text-base">arrow_drop_down</span>
          </summary>
          <form method="post" action="/perfil/cambiar-idioma" class="mt-1">
            <input type="hidden" name="locale" value={otherLang} />
            <button type="submit" class="px-3 py-1 rounded-full border text-xs uppercase">
              {otherLang}
            </button>
          </form>
        </details>
      </div>
    </div>

    <!-- Logout -->
    <div class="self-start">
      <button
        class="btn  bg-error-400 text-error-contrast-700"
        on:click={logUserOut}>
        {$t('profile.logout')}
      </button>
    </div>
  </section>
</div>
{/if}