<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import {  t } from '$i18';
  import Spinner from '$lib/components/global/Spinner.svelte';
  import Title from '$lib/components/global/Title.svelte';
  import BigButton from '$lib/components/global/BigButton.svelte';
  import NoResults from '$lib/components/global/pagedResults/NoResults.svelte';
  import ConfirmModal from '$lib/components/global/modal/ConfirmModal.svelte';
  import { getPath, goBack } from '$lib/navigation/pageInfo';
  import { Service } from '$models/Service';
  import { deleteBusiness, updateBusiness } from '$services/businessService';
  import { getServices } from '$services/serviceService';
  import Icon from '$icons';
  import ImageWithFallback from '$lib/components/global/ImageWithFallback.svelte';
  import BigButtonWarning from '$lib/components/global/BigButtonWarning.svelte';
  import type { BusinessFormErrors } from '$models/forms/BusinessCreationForm';
  import PaginationControls from '$lib/components/global/pagedResults/PaginationControls.svelte';
  import { setPage } from '$lib/navigation/changePage.js';
  import {BusinessUpdateForm, type BusinessUpdateFormErrors} from "$models/forms/BusinessUpdateForm";


  let { data } = $props()
  let { business, servicesList, isOwner, pageNum } = data
  let pageNumD = $state(pageNum)

  
  //let servicesList: PagedResult<Service> | null = null;
  let loading = $state(false);
  let editing = $state(false);

  // si el backend ya te trae esto, podés reemplazarlo por business.isOwner

  // rating promedio (ya lo tenés en Business.rating)
  let avgRating = business?.rating ?? 0;
  let hasRating = avgRating > 0;

  let businessEditInfo = $state({
    businessEmail: '',
    businessTelephone: '',
    businessLocation: ''
  });

  let updateFormErrors: BusinessUpdateFormErrors = $state({});


  // modal borrar negocio
  let showDeleteModal = $state(false);
  let formErrors :BusinessFormErrors = $state({businessName:business.businessName}
)
  onMount(() => {
     loadBusinessInfoToUpdate()
  });

  async function loadServices() {
    loading = true
    servicesList = await getServices({businessId: business.businessId, page:pageNumD})
    loading = false
  }

  function loadBusinessInfoToUpdate() {
    businessEditInfo = {
      businessEmail: business.email,
      businessTelephone: business.telephone,
      businessLocation: business.address
    };
    updateFormErrors = {};
  }


  function toggleEdit() {
    loadBusinessInfoToUpdate()
    editing = !editing;
  }

  async function handleSaveBusiness() {
    const form = new BusinessUpdateForm(
            businessEditInfo.businessEmail,
            businessEditInfo.businessTelephone,
            businessEditInfo.businessLocation
    );

    const errors = form.validateBusinessUpdateForm();

    if (Object.keys(errors).length > 0) {
      updateFormErrors = errors;
      return;
    }

    try {
      loading = true;
      await updateBusiness(business.businessId, businessEditInfo);
      editing = false;
      updateFormErrors = {};
    } finally {
      loading = false;
    }
  }


  async function handleDeleteBusiness() {
    if (!business) return;
    try {
      await deleteBusiness(business.businessId);
      showDeleteModal = false;
      // redirigimos a listado de negocios o home
      await goto(getPath('/my-businesses'));
    } catch (e) {
      console.error(e);
      // TODO: toaster de error
    }
  }

  async function moveToPage(newPage:number) {
    
    pageNumD = newPage
   
    loadServices()
    await setPage(newPage)
   
  }
</script>

{#if loading}
  <Spinner />
{:else if business}
  <div class="w-full flex flex-col gap-6 px-4 py-6">

    <!-- Back + header -->
	 
    <header class="flex items-center justify-between gap-4">
      <div class="flex  gap-3">
      <button
          type="button"
          onclick={() => goBack(isOwner? "my-businesses":"/")}
		  class="btn bg-surface-100"
        >
        <Icon name="leftArrow"/>
        </button>  
        <Title text={business.businessName} />
      </div>

      <!-- zona derecha: rating / acciones -->
      <div class="flex items-center gap-3">
        {#if !isOwner}
          <!-- No owner: ver reseñas + rating -->
          {#if hasRating}
            <a href={getPath(`/businesses/${business.businessId}/reviews`)}>
              <button class="px-3 py-1 rounded-full border text-sm hover:bg-slate-50">
                {$t('review.see')}
              </button>
            </a>
          {/if}
          <p class="flex items-center gap-1 text-sm">
            <span class="font-semibold">
              {hasRating ? avgRating.toFixed(1) : $t('service.unrated')}
            </span>
            <Icon name="star"/>
          </p>
        {:else}
          <!-- Owner: botones de gestión -->
          <div class="flex items-center gap-2">
            <!-- borrar negocio -->
            <BigButtonWarning
				iconName="delete"
				title=""
              onclick={() => (showDeleteModal = true)}
            />


            <!-- crear servicio -->
            <a href={getPath(`/businesses/${business.businessId}/create-service`)}>
              <BigButton iconName="add" title={$t('business.add-service')} />
            </a>

            <!-- ver turnos (solo si hay servicios) -->
            {#if servicesList && servicesList.items.length > 0}
              <a href={getPath(`/businesses/${business.businessId}/appointments`)}>
                <BigButton iconName="calendar" title={$t('business.appointments')} />
              </a>
            {/if}
          </div>
        {/if}
      </div>
    </header>

    <!-- Rating para owner (como en JSP) -->
    {#if isOwner}
      <div class="flex items-center justify-end gap-2 pr-4">
        <p class="flex items-center gap-1 text-sm">
          <span class="font-semibold">
            {hasRating ? avgRating.toFixed(1) : $t('service.unrated')}
          </span>
          
		  <Icon name="star"/>
        </p>
        {#if hasRating}
          <a href={getPath(`/businesses/${business.businessId}/reviews`)}>
            <button class="px-3 py-1 rounded-full border text-sm hover:bg-slate-50">
              {$t('review.see')}
            </button>
          </a>
        {/if}
      </div>
    {/if}

    <!-- Bloque info de contacto -->
    <section class="rounded-2xl shadow bg-white p-5 space-y-4">
      <div class="flex items-center justify-between">
        <h3 class="text-lg font-semibold">
          {$t('business.contact')}
        </h3>

        {#if isOwner}
          <button
            type="button"
            class="text-sm px-3 py-1 rounded-full border hover:bg-slate-50"
            onclick={toggleEdit}
          >
            {editing ? $t('service.cancel') : $t('business.edit')}
          </button>
        {/if}
      </div>

      <!-- modo vista -->
      {#if !editing}
        <div class="space-y-2 text-sm">
          <p class="flex items-center gap-2">
            <Icon name="mail"/>
            <span>{businessEditInfo.businessEmail}</span>
            
          </p>
          <p class="flex items-center gap-2">
            <Icon name="phone"/>
            <span>{businessEditInfo.businessTelephone}</span>
          </p>
          {#if business.address}
            <p class="flex items-center gap-2">
              <Icon name="location"/>
              <span>{businessEditInfo.businessLocation}</span>
            </p>
          {/if}
        </div>
      {:else}
        <!-- modo edición -->
        <div class="space-y-3 text-sm">
          <div class="flex items-center gap-2">
            <Icon name="mail"/>
            <input
              type="text"
              class="flex-1 border rounded-lg px-3 py-1"
              bind:value={businessEditInfo.businessEmail}
              placeholder={$t('business-email')}
            />
          </div>
          {#if updateFormErrors.businessEmail}
            <p class="text-xs text-red-500 ml-8">{ $t(updateFormErrors.businessEmail) }</p>
          {/if}
          <div class="flex items-center gap-2">
            <Icon name="phone"/>
            <input
              type="text"
              class="flex-1 border rounded-lg px-3 py-1"
              bind:value={businessEditInfo.businessTelephone}
              placeholder={$t('telephone')}
            />
          </div>
          {#if updateFormErrors.businessTelephone}
            <p class="text-xs text-red-500 ml-8">{ $t(updateFormErrors.businessTelephone) }</p>
          {/if}
          <div class="flex items-center gap-2">
            <Icon name="location"/>
            <input
              type="text"
              class="flex-1 border rounded-lg px-3 py-1"
              bind:value={businessEditInfo.businessLocation}
              placeholder={$t('address')}
            />
          </div>
          {#if updateFormErrors.businessLocation}
            <p class="text-xs text-red-500 ml-8">{ $t(updateFormErrors.businessLocation) }</p>
          {/if}

          <div class="flex justify-center pt-2">
            <button
              type="button"
              class="px-5 py-2 rounded-full bg-primary-500 text-white text-sm font-semibold hover:bg-primary-600"
              onclick={handleSaveBusiness}
            >
              {$t('business.save-changes')}
            </button>
          </div>
        </div>
      {/if}
    </section>

    <!-- Servicios -->
    <section class="space-y-3">
      <h3 class="text-lg font-semibold px-1">
        {$t('business.services')}
      </h3>

      {#if servicesList && servicesList.items.length > 0}
        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
          {#each servicesList.items as service}
            <a
              class="no-underline"
              href={getPath(`/services/${service.serviceId}`)}
            >
              <div class="rounded-2xl shadow bg-white overflow-hidden hover:shadow-lg transition flex flex-col">
                <ImageWithFallback
				src={service.getServiceImageUrl()}
				fallback={Service.getFallbackImage()}
				/>

                <p class="px-4 py-3 text-sm font-medium truncate">
                  {service.serviceName}
                </p>
              </div>
            </a>
          {/each}
        </div>
        <PaginationControls 
          page={pageNumD} 
          pagedList = {servicesList}
          onPageChange={(newPage) => moveToPage(newPage)  }/>
  
      {:else} 
        <NoResults
          message={$t('business.not-found')}
          actionUrl={getPath(`/businesses/${business.businessId}/create-service`)}
          actionLabel={`+ ${$t('business.add-service')}`}
        />
      {/if}
    </section>
  </div>

  <!-- Modal borrar negocio -->
  <ConfirmModal
    open={showDeleteModal}
    title={$t('popup.business.title')}
    message={$t('popup.business.message')}
    cancelLabel={$t('service.cancel')}
    confirmLabel={$t('popup.delete')}
    on:cancel={() => (showDeleteModal = false)}
    on:confirm={handleDeleteBusiness}
  />
{:else}
  <NoResults message={$t('global.no-results')} />
{/if}
