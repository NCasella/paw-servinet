<script lang="ts">
	import BigButton from "$lib/components/global/BigButton.svelte";
    import { t } from "$lib/i18n/i18n"
	import { getCurrentUser } from "$services/userService";
	import { onMount } from "svelte";
    import { BusinessForm, type BusinessFormErrors } from "$models/forms/BusinessCreationForm";
	import { createBusiness } from "$services/businessService";
	import { isFetchError } from "$utils/apiFetch";
    import FormError from "$lib/components/global/forms/FormError.svelte"
	import { createToaster } from "@skeletonlabs/skeleton-svelte";
  import { base } from '$app/paths';
  import { goto } from '$app/navigation';

let postUrl, businessForm :BusinessForm, email =""
const toaster = createToaster()
let formErrors :BusinessFormErrors = {businessName:""}

async function handleSubmit() {
   formErrors  = businessForm.validateBusinessForm()
    if (formErrors == null) return

    const businessId = await createBusiness(businessForm)
    goto(`${base}/businesses/${businessId}`)

}

onMount(() =>{
    getCurrentUser().then( (u)=> {
        email = u.email;
        businessForm = new BusinessForm( email, "", "", "")
    } )
})


</script>
{#if businessForm}
    <div class="flex justify-center px-4 py-8">
  <form
    class="w-full max-w-md rounded-2xl shadow p-6 space-y-6"
    on:submit|preventDefault={handleSubmit}
  >
    <h2 class="text-xl font-semibold">
      {$t('register.create-business')}
    </h2>

    <!-- Nombre del negocio -->
    <div class="space-y-1">
      <label for="businessName" class="block text-sm font-medium">
        {$t('business-name')}
      </label>
      <input
        id="businessName"
        name="businessName"
        type="text"
        class="w-full border rounded-lg px-3 py-2 text-sm"
        placeholder={$t('input.business')}
        bind:value={businessForm.businessName}
      />
      {#if formErrors.businessName}
        <FormError errorMessage={formErrors.businessName} />
        {/if}
    </div>
        

    <!-- Email del negocio -->
    <div class="space-y-1">
      <label for="businessEmail" class="block text-sm font-medium">
        {$t('business-email')}
      </label>
      <input
        id="businessEmail"
        name="businessEmail"
        class="w-full border rounded-lg px-3 py-2 text-sm"
        placeholder={$t('input.business-email')}
        bind:value={businessForm.businessEmail}
      />
      {#if formErrors.businessEmail}
        <FormError errorMessage={formErrors.businessEmail} />
        {/if}
    </div>
    

    <!-- Teléfono -->
    <div class="space-y-1">
      <label for="businessTelephone" class="block text-sm font-medium">
        {$t('telephone')}
      </label>
      <input
        id="businessTelephone"
        name="businessTelephone"
        type="text"
        class="w-full border rounded-lg px-3 py-2 text-sm"
        placeholder={$t('input.telephone')}
        bind:value={businessForm.businessTelephone}
        />
    {#if formErrors.businessTelephone}
        <FormError errorMessage={formErrors.businessTelephone} />
        {/if}
    </div>

    <!-- Dirección -->
    <div class="space-y-1">
      <label for="businessLocation" class="block text-sm font-medium">
        {$t('address')}
      </label>
      <input
        id="businessLocation"
        name="businessLocation"
        type="text"
        class="w-full border rounded-lg px-3 py-2 text-sm"
        placeholder={$t('input.business-address')}
        bind:value={businessForm.businessLocation}
      />
      {#if formErrors.businessLocation}
        <FormError errorMessage={formErrors.businessLocation} />
        {/if}
    </div>

    <!-- Submit -->
    <div class="flex justify-center pt-2">
      <BigButton title={$t('register.submit')} iconName=""/>
    </div>
  </form>
</div>
{/if}

