<script lang="ts">
	import { t } from '$lib/i18n/i18n';
	import { onMount } from 'svelte';
	import { page } from '$app/stores';
	import { base } from '$app/paths';
    import { goto } from '$app/navigation';
    import { getCurrentUser } from '$services/userService';
	import { loginWithBasicAuth } from '$services/authenticate';
    import { UserContactInfo } from '$models/ContactInfo';
	import Spinner from "$lib/components/global/Spinner.svelte";
    import type { PageData } from './$types';

    export let data :PageData
    let { user, otherLang } = data
    let id = '';
    let verifying = true;
    let verificationSuccess = false;
    let errorMessage = '';

onMount(async () => {
    id = $page.params.id;
    
    if (user === null) {
        verifying = false;
        errorMessage = $t('verification.error.no-user');
        return;
    }
    
    const ok = await loginWithBasicAuth(user.email, id);
    verifying = false;
    
    if (ok) {
        verificationSuccess = true;
        setTimeout(() => {
            goto(`${base}/profile`);
        }, 2000);
    } else {
        errorMessage = $t('verification.error.failed');
    }
});
</script>

<div class="flex min-h-full flex-col justify-center items-center px-6 py-12">
    {#if verifying}
        <Spinner/>
    {:else if verificationSuccess}
        <div class="text-center">
            <svg class="mx-auto h-16 w-16 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <h2 class="mt-6 text-2xl font-bold text-gray-900">{$t('verification.success.title')}</h2>
            <p class="mt-2 text-gray-600">{$t('verification.success')}</p>
        </div>
    {:else}
        <div class="text-center">
            <svg class="mx-auto h-16 w-16 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <h2 class="mt-6 text-2xl font-bold text-gray-900">{$t('verification.error.failed')}</h2>
            <p class="mt-2 text-gray-600">{errorMessage}</p>
            <a href="{base}/" class="mt-6 inline-block text-indigo-600 hover:text-indigo-500">
                {$t('error.backtohome')}
            </a>
        </div>
    {/if}
</div>