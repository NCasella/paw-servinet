<script lang="ts">
    import { t } from "$lib/i18n/i18n";
    import { onMount } from "svelte";
    import { RegisterUserForm, type RegisterUserFormErrors, type RegisterUserFormData } from "$models/forms/UserCreationForm";
    import { createUser, getCurrentUser } from "$services/userService";
    import FormError from "$lib/components/global/forms/FormError.svelte";
    import { base } from '$app/paths';
    import { goto } from '$app/navigation';
	import { loginWithBasicAuth } from "$services/authenticate";

    let showPassword: boolean = false;
    let showConfirmPassword: boolean = false;
    let registerForm: RegisterUserForm;
    let formErrors: RegisterUserFormErrors = {}; 
    let loading: boolean = false;

    async function handleSubmit() {
        loading = true;
        
        formErrors = registerForm.validateRegisterUserForm();

        const hasErrors = Object.values(formErrors).some(error => error && error.trim() !== "");
        
        if (hasErrors) {
            loading = false;
            return;
        }

        try {
            await createUser(registerForm); 

            const ok = await loginWithBasicAuth(registerForm.email, registerForm.password);

            loading = false;

            if (ok) {
              await goto(`${base}/profile`);
            }

        } catch (error) {
            console.error("Error during registration:", error);
        } finally {
            loading = false;
        }
    }

    onMount(() => {
        registerForm = new RegisterUserForm("", "", "", "", "", "", "");
    });
</script>

{#if registerForm}
<div class="flex min-h-full flex-col justify-center px-6 py-12 lg:px-8">
        <div class="sm:mx-auto sm:w-full sm:max-w-sm">

      <h2 class="mt-10 text-center text-2xl/9 font-bold tracking-tight">
                {$t('register.create-profile')}
            </h2>
        </div>

        <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
    <form on:submit|preventDefault={handleSubmit} class="space-y-6">
                <div>
                    <label for="name" class="block text-sm/6 font-medium">{$t('name')}</label>
                    <div class="mt-2">
                        <input
                            id="name"
                            bind:value={registerForm.name}
                            type="text"
                            name="name"
                            placeholder={$t('input.name')}
                            required
                            class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6"
                        />
                    </div>
                    {#if formErrors.name}
                        <FormError errorMessage={formErrors.name} />
                    {/if}
                </div>

                <div>
                    <label for="surname" class="block text-sm/6 font-medium">{$t('lastname')}</label>
                    <div class="mt-2">
                        <input
                            id="surname"
                            bind:value={registerForm.surname}
                            type="text"
                            name="surname"
                            placeholder={$t('input.lastname')}
                            required
                            class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6"
                        />
                    </div>
                    {#if formErrors.surname}
                        <FormError errorMessage={formErrors.surname} />
                    {/if}
                </div>

                <div>
                    <label for="email" class="block text-sm/6 font-medium">{$t('email')}</label>
                    <div class="mt-2">
                        <input
                            id="email"
                            bind:value={registerForm.email}
                            type="email"
                            name="email"
                            placeholder={$t('input.email')}
                            required
                            class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6"
                        />
                    </div>
                    {#if formErrors.email}
                        <FormError errorMessage={formErrors.email} />
                    {/if}
                </div>

                <div>
                    <label for="telephone" class="block text-sm/6 font-medium">{$t('telephone')}</label>
                    <div class="mt-2">
                        <input
                            id="telephone"
                            bind:value={registerForm.telephone}
                            type="tel"
                            name="telephone"
                            placeholder={$t('input.telephone')}
                            required
                            class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6"
                        />
                    </div>
                    {#if formErrors.telephone}
                        <FormError errorMessage={formErrors.telephone} />
                    {/if}
                </div>

                <div>
                    <label for="username" class="block text-sm/6 font-medium">{$t('username')}</label>
                    <div class="mt-2">
                        <input
                            id="username"
                            bind:value={registerForm.username}
                            type="text"
                            name="username"
                            placeholder={$t('input.username')}
                            required
                            class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6"
                        />
                    </div>
                    {#if formErrors.username}
                        <FormError errorMessage={formErrors.username} />
                    {/if}
                </div>
                
                <div>
                    <label for="password" class="block text-sm/6 font-medium">{$t('password')}</label>
                    <div class="mt-2 relative">
                    	<input
                    	    id="password"
                    	    bind:value={registerForm.password}
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
                    {#if formErrors.password}
                        <FormError errorMessage={formErrors.password} />
                    {/if}
                </div>

                <div>
                    <label for="confirmPassword" class="block text-sm/6 font-medium">{$t('repeat-password')}</label>
                    <div class="mt-2 relative">
                        <input
                            id="confirmPassword"
                            bind:value={registerForm.confirmPassword}
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
                        disabled={loading} 
                        class="flex w-full justify-center rounded-md bg-primary-500 px-3 py-1.5 text-sm/6 font-semibold text-white shadow-xs hover:bg-indigo-500 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                    {#if loading}
                        <svg class="animate-spin h-5 w-5 mr-3 border-2 border-white border-t-transparent rounded-full" viewBox="0 0 24 24"></svg>
                    {:else}
                        {$t('register.submit')}
                    {/if}

                    </button>
                </div>
     

            <p class="mt-10 text-center text-sm/6 text-surface">
                {$t('register.already-registered')}
                <a href={`${base}/login`} class="font-semibold text-primary-400 underline">
                    {$t('login')}
                </a>
            </p>
            </form>
        </div>
    </div>
           
{/if}