<script lang="ts">
  import Icon from '$icons';
  import type { PagedResult } from '$models/PagedList'

  export let pagedList: PagedResult<any>;
  export let page = 1;
  export let onPageChange: (page: number) => void;

  type PageItem = number | 'ellipsis';

  // Calculamos la última página.
  // Usamos links.last o links.total como fallback.
  $: lastPage = (() => {
    const raw = pagedList?.links?.last ?? pagedList?.links?.total ?? 1;
    const n =
      typeof raw === 'string'
        ? Number(raw)
        : typeof raw === 'number'
        ? raw
        : 1;
    return Number.isFinite(n) && n > 0 ? n : 1;
  })();

  // Lógica de páginas (como la de tu amiga)
  $: pages = (() => {
    const last = lastPage;
    const current = page;

    if (last <= 5) {
      return Array.from({ length: last }, (_, i) => i + 1) as PageItem[];
    } else if (current <= 2 || current === last) {
      return [1, 2, 'ellipsis', last] as PageItem[];
    } else {
      return [1, 'ellipsis', current - 1, current, current + 1, 'ellipsis', last] as PageItem[];
    }
  })();

  function goToPage(p: number) {
    if (p < 1 || p > lastPage || p === page) return;
    onPageChange?.(p);
  }

  function prev() {
    goToPage(page - 1);
  }

  function next() {
    goToPage(page + 1);
  }
</script>

{#if lastPage > 1}
  <div class="flex justify-center items-center space-x-2 my-4">
    <!-- Prev -->
    <button
      type="button"
      class="w-8 h-8 flex items-center justify-center rounded-full
             border border-slate-200 text-slate-600 hover:bg-slate-50
             disabled:opacity-40 disabled:cursor-default"
      on:click={prev}
      disabled={page <= 1}
      aria-label="Previous page"
    >
      <Icon name="leftArrow" />
    </button>

    <!-- Números -->
    <div class="flex justify-center items-center space-x-2">
      {#each pages as p}
        {#if p === 'ellipsis'}
          <span class="w-8 h-8 flex items-center justify-center text-slate-400">…</span>
        {:else}
          <button
            type="button"
            class="w-8 h-8 flex items-center justify-center rounded
                   cursor-pointer text-sm
                   transition
                   {p === page
                     ? 'bg-primary-500 text-white font-bold shadow'
                     : 'bg-slate-200 text-slate-800 hover:bg-slate-300'}"
            on:click={() => goToPage(p)}
          >
            {p}
          </button>
        {/if}
      {/each}
    </div>

    <!-- Next -->
    <button
      type="button"
      class="w-8 h-8 flex items-center justify-center rounded-full
             border border-slate-200 text-slate-600 hover:bg-slate-50
             disabled:opacity-40 disabled:cursor-default"
      on:click={next}
      disabled={page >= lastPage}
      aria-label="Next page"
    >
      <Icon name="rightArrow" />
    </button>
  </div>
{/if}
