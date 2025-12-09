<script lang="ts">
	import { asset, assets } from "$app/paths";
	import { text } from "@sveltejs/kit";
	import BigButton from "../BigButton.svelte";


  export let message: string = "";            // mensaje opcional
  export let actionUrl: string | null = null; // URL del call to action
  export let actionLabel: string = "";        // label del botón
</script>

<div class="flex flex-col items-center justify-center text-center py-10 gap-4">

  <!-- Imagen -->
  <div class="w-40 h-40">
    <img 
      src={asset("/images/noResults.svg")}
      alt="No results"
      class="w-full h-full object-contain opacity-80"
    />
  </div>

  <!-- Texto -->
  <p class="text-lg font-medium text-gray-600 px-4">
    {message}
  </p>

  <!-- Botón opcional -->
  {#if actionUrl}
    <a href={actionUrl} class="mt-2">
      <BigButton
        title={actionLabel} iconName=""/>
    </a>
  {/if}

</div>
