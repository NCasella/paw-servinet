<script lang="ts">
    import Title from "$lib/components/global/Title.svelte";
    import BigButton from "$lib/components/global/BigButton.svelte";
    import {t} from "$lib/i18n/i18n"
    import { base } from "$app/paths";
    import type {User} from "$models/User";
    import {onMount} from "svelte";
    import {closeSession, getCurrentUser} from "$services/userService";
    import {goto} from "$app/navigation";
    import type {Service} from "$models/Service";


</script>
<header class="mx-8 flex place-content-between items-baseline">
    <Title text={$t("businesses.my-businesses")}/>
</header>