<script>
    import {t} from '$lib/i18n/i18n'
    let {errorMessage} = $props()
</script>
<p class="text-error-500">{$t(errorMessage)}</p>