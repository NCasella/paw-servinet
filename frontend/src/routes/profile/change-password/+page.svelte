<script lang="ts">
    import { onMount } from "svelte";
    import {setLanguage, t} from "$i18";
    import FormError from "$lib/components/global/forms/FormError.svelte";
    import BigButton from "$lib/components/global/BigButton.svelte";
    import { goto } from "$app/navigation";
    import { base } from "$app/paths";

    import { UserUpdateForm }
        from "$models/forms/UserUpdateForm";
    import { UserPasswordUpdateForm }
        from "$models/forms/UserPasswordUpdateForm";
    import type { UserPasswordUpdateFormErrors }
        from "$models/forms/UserPasswordUpdateForm";
    import {getCurrentUserContactInfo, updateUser} from "$services/userService";
    import type {UserContactInfo} from "$models/ContactInfo";
	import BigButtonSecondaryWithOnClick from "$lib/components/global/BigButtonSecondaryWithOnClick.svelte";

    let userForm: UserUpdateForm;
    let passForm: UserPasswordUpdateForm;
    let formErrors: UserPasswordUpdateFormErrors = {};
    let showNewPassword = false;
    let showRepeatPassword = false;


    let user: UserContactInfo;

    onMount(async () => {
        user = await getCurrentUserContactInfo();

        passForm = new UserPasswordUpdateForm("", "");

        userForm = new UserUpdateForm({
            username: user.username,
            email: user.email,
            telephone: user.telephone,
            locale: user.language,
            password: ""
        });
    });

    async function handleSubmit(event: Event) {
        event.preventDefault();
    
        formErrors = passForm.validate();
        if (Object.keys(formErrors).length > 0) return;
    
        try {
            userForm.password = passForm.newPassword;
            await updateUser(user.userId, userForm);
            goto(`${base}/profile`);
        } catch (e) {}
    }

</script>

<svelte:head>
    <title>{$t('title.change-password')}</title>
</svelte:head>

{#if userForm}
    <div class="flex justify-center px-4 py-8">
        <form
            class="w-full max-w-xl rounded-2xl shadow p-6 space-y-6"
            onsubmit={handleSubmit}
        >

            <h2 class="text-xl font-semibold">
                {$t('profile.change-password.title')}
            </h2>

            <!-- new password -->
            <div class="space-y-1">
                <label class="block text-sm font-medium">
                    {$t('profile.new-password')}
                </label>
            
                <div class="relative">
                    <input
                        type={showNewPassword ? "text" : "password"}
                        class="w-full border rounded-lg px-3 py-2 pr-10 text-sm"
                        bind:value={passForm.newPassword}
                    />
                
                    <button
                        type="button"
                        onclick={() => showNewPassword = !showNewPassword}
                        class="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-400 hover:text-gray-600"
                    >
                        {#if showNewPassword}
                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                    d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243" />
                            </svg>
                        {:else}
                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                    d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                            </svg>
                        {/if}
                    </button>
                </div>
            
                {#if formErrors.newPassword}
                    <FormError errorMessage={formErrors.newPassword} />
                {/if}
            </div>


            <!-- repeat new password -->
            <div class="space-y-1">
                <label class="block text-sm font-medium">
                    {$t('profile.repeat-new-password')}
                </label>
            
                <div class="relative">
                    <input
                        type={showRepeatPassword ? "text" : "password"}
                        class="w-full border rounded-lg px-3 py-2 pr-10 text-sm"
                        bind:value={passForm.confirmNewPassword}
                    />
                
                    <button
                        type="button"
                        onclick={() => showRepeatPassword = !showRepeatPassword}
                        class="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-400 hover:text-gray-600"
                    >
                        {#if showRepeatPassword}
                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                    d="M13.875 18.825A10.05 10.05 0 0112 19" />
                            </svg>
                        {:else}
                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                    d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                            </svg>
                        {/if}
                    </button>
                </div>
            
                {#if formErrors.confirmNewPassword}
                    <FormError errorMessage={formErrors.confirmNewPassword} />
                {/if}
            </div>


            <div class="flex justify-end gap-3 pt-2">
                <BigButtonSecondaryWithOnClick
                    title={$t('profile.cancel')}
                    iconName=""
                    onclick={goto(`${base}/profile`)}
                />
                <BigButton
                    title={$t('profile.update-password')}
                    iconName=""
                /> 
            </div>

        </form>
    </div>
{/if}
