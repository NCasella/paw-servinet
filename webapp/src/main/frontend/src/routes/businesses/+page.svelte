<script lang="ts">
    import Title from "$lib/components/global/Title.svelte";
	import BigButton from "$lib/components/global/BigButton.svelte";
    import {t} from "$lib/i18n/i18n"
	import { base } from "$app/paths";
    
</script>
<header class="ml-8 mr-8 flex place-content-between items-baseline">
    <Title text={$t("businesses.my-businesses")}/>
    <div class="flex flex-row-reverse l-4">
        <BigButton iconName="notification" title={$t("businesses.questions")}/>
        <BigButton iconName="calendar" title={$t("businesses.appointments")}/>
        <a href="{base}/create-business">
        <BigButton iconName="business" title={$t("businesses.add-business")}/>
        </a>
    </div>
</header>

