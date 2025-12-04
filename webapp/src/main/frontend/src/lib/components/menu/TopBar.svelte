<script lang="ts">
	import { page } from '$app/state';
	import { goto } from '$app/navigation';
    import { base } from '$app/paths';
    import UserMenu from '$lib/components/menu/UserMenu.svelte';
    import {t} from "$lib/i18n/i18n"
	import { user } from '$stores/userStore';
	import { onMount } from 'svelte';
	import { getCurrentUser } from '$services/userService';
	import { type User } from '$models/User';

	let currentUser :User | null;
	onMount(async () => {
		try {
		currentUser = await getCurrentUser();
		console.log(currentUser.profilePicture)
		} catch {
		currentUser = null;
    }})

	let isProvider = true

	function isRouteActive(path: string): boolean {
	  return page.url.pathname === path;
	}
  
	async function handleLogin(event: Event): Promise<void> {
        goto('login');
    }
    
    async function handleLogout(event: Event): Promise<void> {
	  event.preventDefault();
	
	  const response: Response = await fetch('/api/logout', {
		method: 'POST',
	  });
  
	  const result = await response.json();
  
	  if (response.ok) {
		alert('Logout successful');
		//userStore.set({ authUser: null, session: null });
		goto('login');
	  } else {
		alert('Error logging out: ' + result.error);
	  }
	}
  
  </script>
  
  <header class="top-bar">
	<div class="top-bar__left">
	  <a class="logo" href={base}>
        <h1 class="text-4xl text-primary-500 font-bold"> servinet .</h1>
		<!--img src='/images/servinet.png' alt="Logo"  /-->
	  </a>
	</div>
	<div class="top-bar__right">
      <button type="button" class="btn "><a href="/services"> {$t("navbar.all-services")}</a></button>
	  {#if $user.user }
		<UserMenu user={currentUser}/>  	
	  {:else}
	  <button type="button" class="btn preset-filled-primary-500" on:click={handleLogin}>{$t("login")}</button>
		{/if}
	</div>
	
  </header>
  
  <style lang="scss">
	.top-bar {
	  display: flex;
	  justify-content: space-between;
	  align-items: center;
	  padding: 1rem 2rem;
	}
  
	.top-bar__left {
	  display: flex;
	  gap: 1rem;
	  justify-content: flex-start;
	  align-items: center;
	}
  
	.top-bar__right {
	  display: flex;
	  align-items: center;
	  gap: 1rem;
	}
  
	.logo {
	  display: flex;
	  align-items: center;
	  justify-content: center;
	}
  
  </style>
  
