<script lang="ts">
    import { onMount } from "svelte";
    import {setLanguage, t} from "$i18";
    import FormError from "$lib/components/global/forms/FormError.svelte";
    import BigButton from "$lib/components/global/BigButton.svelte";
    import { goto } from "$app/navigation";
    import { base } from "$app/paths";

    import { UserUpdateForm }
        from "$models/forms/UserUpdateForm";
    import type { UserUpdateFormErrors }
        from "$models/forms/UserUpdateForm";
    import {getCurrentUserContactInfo, updateUser} from "$services/userService";
    import type {UserContactInfo} from "$models/ContactInfo";

    let userForm: UserUpdateForm;
    let formErrors: UserUpdateFormErrors = {};

    let user: UserContactInfo;

    onMount(async () => {
        user = await getCurrentUserContactInfo();

        userForm = new UserUpdateForm({
            username: user.username,
            email: user.email,
            telephone: user.telephone,
            locale: user.language,
        });
    });

    async function handleSubmit() {
        formErrors = userForm.validate();
        if (Object.keys(formErrors).length > 0) return;

        try {
            await updateUser(user.userId, userForm);
            if(userForm.locale !== user.language) {
                setLanguage(userForm.locale);
            }
            goto(`${base}/profile`);
        } catch (e) {
            if(e.status == 400) {
                formErrors.email = 'profile.repeated-email-or-username';
            }
        }
    }
</script>

{#if userForm}
    <div class="flex justify-center px-4 py-8">
        <form
                class="w-full max-w-xl rounded-2xl shadow p-6 space-y-6"
                on:submit|preventDefault={handleSubmit}
        >
            <h2 class="text-xl font-semibold">
                {$t('profile.edit')}
            </h2>

            <!-- Username -->
            <div class="space-y-1">
                <label class="block text-sm font-medium">
                    {$t('username')}
                </label>
                <input
                        type="text"
                        class="w-full border rounded-lg px-3 py-2 text-sm"
                        bind:value={userForm.username}
                />
                {#if formErrors.username}
                    <FormError errorMessage={formErrors.username} />
                {/if}
            </div>

            <!-- Email -->
            <div class="space-y-1">
                <label class="block text-sm font-medium">
                    {$t('email')}
                </label>
                <input
                        type="email"
                        class="w-full border rounded-lg px-3 py-2 text-sm"
                        bind:value={userForm.email}
                />
                {#if formErrors.email}
                    <FormError errorMessage={formErrors.email} />
                {/if}
            </div>

            <!-- Telephone -->
            <div class="space-y-1">
                <label class="block text-sm font-medium">
                    {$t('telephone')}
                </label>
                <input
                        type="tel"
                        class="w-full border rounded-lg px-3 py-2 text-sm"
                        bind:value={userForm.telephone}
                />
                {#if formErrors.telephone}
                    <FormError errorMessage={formErrors.telephone} />
                {/if}
            </div>

            <!-- Language -->
            <div class="space-y-1">
                <label class="block text-sm font-medium">
                    {$t('profile.favourite-lang')}
                </label>
                <select
                        class="w-full border rounded-lg px-3 py-2 text-sm"
                        bind:value={userForm.locale}
                >
                    <option value="es">ES</option>
                    <option value="en">EN</option>
                </select>
                {#if formErrors.locale}
                    <FormError errorMessage={formErrors.locale} />
                {/if}
            </div>

            <div class="flex justify-center pt-2">
                <BigButton title={$t('service.save-changes')} iconName="" />
            </div>
        </form>
    </div>
{/if}
