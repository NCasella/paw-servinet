<script>
    import Icon from "$icons";
    let {title, iconName,onclick} = $props()
</script>
<button class="btn bg-primary-500 text-white font-bold p-3 text-l" onclick={onclick}>
         {#if iconName}   <Icon name={iconName}/> {/if} {title} </button>