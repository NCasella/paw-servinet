<script lang="ts">
	import { asset } from "$app/paths";
	import { navTo } from "$lib/navigation/pageInfo";
	import {t} from '$i18'
	import BigButtonWithOnClick from "$lib/components/global/BigButtonWithOnClick.svelte";
    import { StatusCodes} from "$models/exceptions/statusCodesEnum"
	import { onMount } from "svelte";
  export let status: number;
  export let error: App.Error;

  function getImageForStatus(){
    /* Unauthorized */
    if(status==StatusCodes.UNAUTHORIZED) 
      navTo('/login') 
    else 
      return asset(`/images/${status}.svg`);
  }

  
</script>

<div class="page flex flex-col items-center justify-center mt-25 gap-10 text-center">

  <img
    class="h-64"
    src={getImageForStatus()}
    alt={`Error ${status}`}
  />

  <h2 class="header-text text-xl font-semibold">
    {error?.message ?? $t("error.badrequest")}
  </h2>

  <div class="align-center">
    <BigButtonWithOnClick onclick={() => navTo("/")} title={$t("error.backtohome")} iconName=""/>
  </div>

</div>

