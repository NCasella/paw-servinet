<script>
    import Icon from "$icons";
    let {title, iconName} = $props()
</script>
<button class="btn border-2 text-error-500 bg-error-100 font-bold p-3 text-l">
    {#if iconName}   <Icon name={iconName}/> {/if} {title} </button>