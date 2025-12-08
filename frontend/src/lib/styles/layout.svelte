<style>
  .container {
    max-width: 1080px;
    margin: 0 auto;
  }
</style>

<div class="container p-4">

    <TopBar/>
  
  <main class="mt-8">
    <slot />
  </main>

</div>

<script>
    import TopBar from "$lib/components/menu/TopBar.svelte";
</script>