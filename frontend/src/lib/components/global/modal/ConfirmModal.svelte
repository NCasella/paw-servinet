<script lang="ts">
  import { createEventDispatcher } from 'svelte';

  export let open = false;
  export let title = '';
  export let message = '';
  export let confirmLabel = 'OK';
  export let cancelLabel = 'Cancel';

  const dispatch = createEventDispatcher();

  function handleCancel() {
    dispatch('cancel');
  }

  function handleConfirm() {
    dispatch('confirm');
  }
</script>

{#if open}
  <div class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center">
    <div class="bg-white rounded-2xl shadow-xl max-w-sm w-full p-6 space-y-4">
      <h2 class="text-lg font-semibold">{title}</h2>
      <p class="text-sm opacity-80">{message}</p>

      <div class="flex justify-end gap-3 pt-2">
        <button
          type="button"
          class="px-3 py-1 text-sm rounded-lg border"
          on:click={handleCancel}
        >
          {cancelLabel}
        </button>
        <button
          type="button"
          class="px-3 py-1 text-sm rounded-lg bg-red-600 text-white"
          on:click={handleConfirm}
        >
          {confirmLabel}
        </button>
      </div>
    </div>
  </div>
{/if}
