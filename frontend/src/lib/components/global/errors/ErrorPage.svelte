<script lang="ts">
	import { navTo } from "$lib/navigation/pageInfo";
	import {t} from '$i18'
	import BigButtonWithOnClick from "$lib/components/global/BigButtonWithOnClick.svelte";
    import { StatusCodes} from "$models/exceptions/statusCodesEnum"
	import Img404 from "$lib/images/404.svg"
  import Img403 from "$lib/images/403.svg"
  import Img400 from "$lib/images/400.svg"
  import Img500 from "$lib/images/500.svg"
  export let status: number;
  export let error: App.Error;

  function getImageForStatus(){
    switch(status){
      case StatusCodes.UNAUTHORIZED:
        navTo('/login'); return;
      case StatusCodes.BAD_REQUEST:
        return Img400
      case StatusCodes.FORBIDDEN:
        return Img403
      case StatusCodes.NOT_FOUND:
        return Img404
      default: return Img500
    }
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

