<script lang="ts">
  export let src: string;
  export let fallback: string;
</script>

<img
  src={src}
  alt=""
  on:error={(e) => {
    const img = e.currentTarget as HTMLImageElement;
    if (!img.dataset.fallback) {
      img.dataset.fallback = "true";
      img.src = fallback;
    }
  }}
  class="w-full h-40 object-cover"
/>
