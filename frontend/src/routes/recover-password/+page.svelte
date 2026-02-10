<script lang="ts">
	import { t } from '$lib/i18n/i18n';
	import { onMount } from 'svelte';
	import { page } from '$app/stores';
	import { base } from '$app/paths';
    import { goto } from '$app/navigation';
	import { resetPassword } from '$services/userService';
    import FormError from "$lib/components/global/forms/FormError.svelte";
	import { ResetPasswordForm, type ResetPasswordFormErrors } from '$models/forms/ResetPasswordForm';
    import { loginWithBasicAuth } from '$services/authenticate';

    const userId = $page.url.searchParams.get('id');
    const code = $page.url.searchParams.get('code');
    //console.log("userId:"+userId);
    //console.log("code:"+code);

    let formErrors: ResetPasswordFormErrors = {};
    let resetPasswordForm: ResetPasswordForm = new ResetPasswordForm("", code || "", "", ""); 
    let email = '';
	let password = '';
    let confirmPassword = ''; 
    let showPassword = false;
    let showConfirmPassword = false;
	let errorMessage = '';
	let loading = false;

	async function handleSubmit(event: Event) {
		event.preventDefault(); 

        resetPasswordForm.email = email;
        resetPasswordForm.password = password;
        resetPasswordForm.confirmPassword = confirmPassword;
        
		errorMessage = '';
        formErrors = resetPasswordForm.validateResetPasswordForm();

        const hasErrors = Object.values(formErrors).some(error => error && error.trim() !== "");
        
        if (hasErrors) {
            loading = false;
            return;
        }

		loading = true;

		const ok = await resetPassword(resetPasswordForm, userId);

		loading = false;

        if (ok) {
            await loginWithBasicAuth(resetPasswordForm.email, resetPasswordForm.password);
            await goto(`${base}/profile`);
        }

        errorMessage = $t('login.passwordrecovery.invalid-email');
	}

</script>

<div class="flex min-h-full flex-col justify-center px-6 py-12 lg:px-8">
	<div class="sm:mx-auto sm:w-full sm:max-w-sm">
		<h2 class="mt-10 text-center text-2xl/9 font-bold tracking-tight">{$t('login.passwordrecovery.title')}</h2>
	</div>

	<div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
        
        <form on:submit|preventDefault={handleSubmit} class="space-y-6">
            <div class="flex items-center justify-between">
				<label for="email" class="block text-sm/6 font-medium">{$t('email')}</label>
			</div>
            <div class="mt-2 relative">
                <input
                id="email"
                bind:value={email}
                    type="email"
                    placeholder={$t('input.email')}
                    name="email"
                    required
                    class="block w-full rounded-md bg-white px-3 py-1.5 pr-10 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6"
                    />
                </div>
                {#if errorMessage}
                    <p class="mb-4 p-3 text-sm text-red-800 rounded-lg bg-red-50">{errorMessage}</p>
                {/if}

            <div>
				<div class="flex items-center justify-between">
					<label for="password" class="block text-sm/6 font-medium">{$t('password')}</label>
				</div>
                <div class="mt-2 relative">
                    <input
                        id="password"
                        bind:value={password}
                        type={showPassword ? "text" : "password"}
                        placeholder={$t('input.recover-password')}
                        name="password"
                        required
                        class="block w-full rounded-md bg-white px-3 py-1.5 pr-10 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6"
                    />
                    <button
                        type="button"
                        on:click={() => showPassword = !showPassword}
                        class="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-400 hover:text-gray-600"
                    >
                        {#if showPassword}
                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                            </svg>
                        {:else}
                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                            </svg>
                        {/if}
                    </button>
                </div>
                {#if formErrors.password}
                    <FormError errorMessage={formErrors.password} />
                {/if}
			</div>

            <div>
				<div class="flex items-center justify-between">
					<label for="confirmPassword" class="block text-sm/6 font-medium">{$t('repeat-password')}</label>
				</div>
                <div class="mt-2 relative">
                    <input
                        id="confirmPassword"
                        bind:value={confirmPassword}
                        type={showConfirmPassword ? "text" : "password"}
                        placeholder={$t('input.recover-repeat-password')}
                        name="confirmPassword"
                        required
                        class="block w-full rounded-md bg-white px-3 py-1.5 pr-10 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6"
                    />
                    <button
                        type="button"
                        on:click={() => showConfirmPassword = !showConfirmPassword}
                        class="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-400 hover:text-gray-600"
                    >
                        {#if showConfirmPassword}
                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                            </svg>
                        {:else}
                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                            </svg>
                        {/if}
                    </button>
			    </div>
                {#if formErrors.confirmPassword}
                    <FormError errorMessage={formErrors.confirmPassword} />
                {/if}
            </div>
            <div>
				<button
					type="submit"
					disabled={loading || !code || !userId}
					class="bg-primary-500 flex w-full justify-center rounded-md px-3 py-1.5 text-sm/6 font-semibold text-white shadow-xs hover:bg-indigo-500 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600 disabled:opacity-50"
				>
                    {#if loading}
                        <svg class="animate-spin h-5 w-5 mr-3 border-2 border-white border-t-transparent rounded-full" viewBox="0 0 24 24"></svg>
                    {:else}
                        {$t('reset-password.submit')}
                    {/if}
                </button>
			</div>
        </form>
    </div>
</div>