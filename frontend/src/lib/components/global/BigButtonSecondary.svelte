<script>
    import Icon from "$icons";
    let {title, iconName} = $props()
</script>
<button class="btn border-2 text-surface-600 font-bold p-3 text-l">
    {#if iconName}   <Icon name={iconName}/> {/if} {title} </button>