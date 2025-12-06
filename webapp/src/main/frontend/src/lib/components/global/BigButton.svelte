<script>
    import Icon from "$icons";
    let {title, iconName} = $props()
</script>
<button class="btn bg-primary-500 text-white font-bold p-3 text-l ml-4">
            <Icon name={iconName}/> {title} </button>