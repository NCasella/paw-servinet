<script lang="ts">
	import { t } from '$lib/i18n/i18n';
    import { onMount } from "svelte";
	import { goto } from '$app/navigation';
	import FormError from "$lib/components/global/forms/FormError.svelte";
	import { loginWithBasicAuth } from '$services/authenticate';
	import { getCurrentUser } from '$services/userService';
	import { base } from '$app/paths';
  	import { requestPasswordRecovery } from '$services/userService';
	import { RequestPasswordRecoveryForm, type RequestPasswordRecoveryFormErrors } from '$models/forms/RequestPasswordRecoveryForm';

	let username = '';
	let password = '';
	let errorMessage = '';
	let loading = false;
	let requestPasswordRecoveryForm: RequestPasswordRecoveryForm;
	let formErrors: RequestPasswordRecoveryFormErrors = {};

	let showPassword = false;
	let showForgotPasswordModal = false;
	let forgotPasswordEmail = '';
	let forgotPasswordMessage = '';
	let loadingForgotPassword = false;

	async function handleSubmit(event: Event) {
		event.preventDefault(); 

		loading = true;
		errorMessage = '';

		const ok = await loginWithBasicAuth(username, password);

		loading = false;

		if (ok) {
			await getCurrentUser()
		
			const canGoBack = window.history.length > 1;
        	const referrer = document.referrer;
        	const currentPath = window.location.pathname;
        	const loginPath = `${base}/login`;

			const isFromLogin = referrer.includes('/login') || referrer === '';
			if (canGoBack && !isFromLogin) {
         	   history.back();
        	} else {
            	goto(`${base}/`);
        	}


			return; 
		}
		errorMessage = 'Invalid username or password';
	}

	function handleForgotPassword(event: Event) {
		event.preventDefault();
		forgotPasswordEmail = username; 
		forgotPasswordMessage = ''; 
		showForgotPasswordModal = true;
	}

	async function sendPasswordReset() {
		loadingForgotPassword = true;
		forgotPasswordMessage = '';
t
    await requestPasswordRecovery({ email: forgoPasswordEmail });

		loadingForgotPassword = false;

		forgotPasswordMessage = `${$t('login.passwordrecovery.success')}`;
		forgotPasswordEmail = ''; 

		// showForgotPasswordModal = false; 
	}

	onMount(() => {
		requestPasswordRecoveryForm = new RequestPasswordRecoveryForm("");
	});
</script>

<div class="flex min-h-full flex-col justify-center px-6 py-12 lg:px-8">
	<div class="sm:mx-auto sm:w-full sm:max-w-sm">
		<h2 class="mt-10 text-center text-2xl/9 font-bold tracking-tight">{$t('login')}</h2>
	</div>

	<div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
		<form on:submit|preventDefault={handleSubmit} class="space-y-6">
			<div>
				<label for="email" class="block text-sm/6 font-medium">{$t('email')}</label>
				<div class="mt-2">
					<input
						id="email"
						bind:value={username}
						type="email"
						name="email"
						placeholder={$t('input.email')}
						required
						autocomplete="email"
						class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6"
					/>
				</div>
			</div>

			<div>
				<div class="flex items-center justify-between">
					<label for="password" class="block text-sm/6 font-medium">{$t('password')}</label>
				</div>
				    <div class="mt-2 relative">
                    	<input
                    	    id="password"
                    	    bind:value={password}
                    	    type={showPassword ? "text" : "password"}
                    	    placeholder={$t('input.password')}
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
			</div>
			{#if errorMessage}
			<div class="p-3 bg-red-100 border border-red-400 text-red-700 rounded-md text-sm" role="alert">
        		{errorMessage}
    		</div>
			{/if}

			<div>
				<div class="flex items-center justify-between">
					<label class="flex items-center space-x-2">
						<input class="checkbox" type="checkbox" checked />
						<p>{$t('login.rememberme')}</p>
					</label>
					<div class="text-sm">
						<a href="#" on:click={handleForgotPassword} class="text-primary-400 font-semibold"
							>{$t('login.forgotpassword')}</a
						>
					</div>
				</div>
			</div>
			<div>
				<button
					type="submit"
					disabled={loading}
					class="bg-primary-500 flex w-full justify-center rounded-md px-3 py-1.5 text-sm/6 font-semibold text-white shadow-xs hover:bg-indigo-500 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
					>{$t('login.submit')}</button
				>
			</div>
		</form>

		<p class="text-surface mt-10 text-center text-sm/6">
			{$t('login.register-1')}
			<a href={`${base}/register`} class="text-primary-400 font-semibold underline"
				>{$t('login.register-2')}</a
			>
		</p>
	</div>

	
</div>

{#if showForgotPasswordModal}
	<div
		class="fixed inset-0 z-50 overflow-y-auto bg-gray-600 transition-opacity duration-300 ease-out"
		aria-labelledby="modal-title"
		role="dialog"
		aria-modal="true"
	>
		<div class="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0">
			<div
				class="relative transform overflow-hidden rounded-lg bg-white text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-lg"
			>
				<div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
					<div class="sm:flex sm:items-start">
						<div class="mt-3 text-center sm:mt-0 sm:ml-4 sm:text-left">
							<h3 class="text-lg/6 leading-6 font-semibold text-gray-900" id="modal-title">
								{$t('login.passwordrecovery.title')}
							</h3>
							<div class="mt-2">
								<p class="text-sm text-gray-500">{$t('login.passwordrecovery.instruction')}</p>

								<form on:submit|preventDefault={sendPasswordReset} class="mt-4 space-y-4">
									<div>
										<label for="forgot-email" class="sr-only">{$t('input.email')}</label>
										<input
											id="forgot-email"
											bind:value={forgotPasswordEmail}
											type="email"
											placeholder={$t('input.email')}
											required
											class="focus:outline-primary-600 block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 sm:text-sm/6"
										/>
									</div>
									{#if formErrors.email}
										<FormError errorMessage={formErrors.email} />
									{/if}

									{#if forgotPasswordMessage}
										<p class="text-sm font-medium text-green-600">{forgotPasswordMessage}</p>
									{/if}

									<div class="flex justify-end space-x-3">
										<button
											type="button"
											on:click={() => (showForgotPasswordModal = false)}
											disabled={loadingForgotPassword}
											class="focus:ring-primary-500 inline-flex justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:ring-2 focus:ring-offset-2 focus:outline-none disabled:opacity-50"
										>
											{$t('cancel')}
										</button>
										<button
											type="submit"
											disabled={loadingForgotPassword || !forgotPasswordEmail}
											class="bg-primary-600 hover:bg-primary-700 focus:ring-primary-500 inline-flex justify-center rounded-md border border-transparent px-4 py-2 text-sm font-medium text-white shadow-sm focus:ring-2 focus:ring-offset-2 focus:outline-none disabled:opacity-50"
										>
											{#if loadingForgotPassword}
                        
											{:else}
												{$t('login.passwordrecovery.send')}
											{/if}
										</button>
									</div>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
{/if}
