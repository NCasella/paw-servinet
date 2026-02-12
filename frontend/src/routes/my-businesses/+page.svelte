<script lang="ts">
    import Title from "$lib/components/global/Title.svelte";
	import BigButton from "$lib/components/global/BigButton.svelte";
    import {t} from "$lib/i18n/i18n"
	import { base } from "$app/paths";
	import { onMount } from "svelte";
	import type { Business } from "$models/Business";
    import type { PagedResult } from "$models/PagedList";
	import { getUserBusinesses } from "$services/businessService";
	import Icon from "$icons";
	import NoResults from "$lib/components/global/pagedResults/NoResults.svelte";
	import PaginationControls from "$lib/components/global/pagedResults/PaginationControls.svelte";
	import { navTo } from "$lib/navigation/pageInfo";
	import type { PageData } from "./$types";
	import { setPage } from "$lib/navigation/changePage";

    let loading =false
    
    export let data :PageData
  $: ({ pagedList, pageNum } = data)



</script>

<svelte:head>
    <title>{$t('title.my-businesses')}</title>
</svelte:head>

<header class="mx-8 flex place-content-between items-baseline">
    <Title text={$t("businesses.my-businesses")}/>
    <div class="flex justify-end l-4 space-x-3">
        <a href="{base}/create-business">
        <BigButton iconName="business" title={$t("businesses.add-business")}/>
        </a>
        <a href="{base}/my-businesses/questions">
        <BigButton iconName="notification" title={$t("businesses.questions")}/>
        </a>
    </div>
</header>

{#if !loading}
<div class="grid p-2 gap-10 sm:grid-cols-2 lg:grid-cols-3">
  {#each pagedList.items as b}
    <div class="card shadow-sm bg-surface p-3 rounded-2xl">
      <div class="card-body flex flex-col gap-3">
        <!-- Nombre del negocio: link a detalle -->
        <a
          href={`${base}/businesses/${b.businessId}`}
          class="flex-1 space-y-1 no-underline"
        >
          <h3 class="text-lg font-semibold truncate">
            {b.businessName}
          </h3>

          {#if b.address}
            <p class="text-sm opacity-80 truncate">
              {b.address}
            </p>
          {/if}
        </a>

        <!-- Footer de la card: ver negocio + crear servicio -->
        <div class="flex items-center justify-between pt-3 border-t text-sm">
          <a
            href={`${base}/businesses/${b.businessId}/create-service`}
            class="btn btn-sm variant-filled flex items-center gap-1"
          >
            <Icon name="add" />
            <span>{$t('business.add-service')}</span>
          </a>
        </div>
      </div>
    </div>
  {/each}
</div>
{#if pagedList.items.length ==0}
    <NoResults />
  {:else }
    <PaginationControls 
    page={pageNum}
    pagedList={pagedList}
    onPageChange={(newPage) => { setPage(newPage)}  }/>
  {/if} 

{/if}