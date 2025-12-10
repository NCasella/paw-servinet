<script lang="ts">
	import { Menu, Portal } from '@skeletonlabs/skeleton-svelte';
    import { t } from "$lib/i18n/i18n";
	import { Avatar } from '@skeletonlabs/skeleton-svelte';
	import { asset, base } from '$app/paths';
	import ImageWithFallback from '$lib/components/global/ImageWithFallback.svelte';
	import { UserContactInfo } from '$models/ContactInfo';
	import { currentUserIsProvider } from '$services/userService';

	let {user} = $props()
	let isProvider = currentUserIsProvider()
	
</script>

<Menu>
	<Menu.Trigger class="btn">
		<Avatar class="h-12 w-12">
				<Avatar.Image src="{user.getProfilePictureSrc()}" alt="base" />
				<Avatar.Fallback><img class="rounded-2xl" src={UserContactInfo.getFallbackImage()}/></Avatar.Fallback>
		</Avatar>
	</Menu.Trigger>
	<Portal>
		<Menu.Positioner>
			<Menu.Content>
				<Menu.ItemGroup>
					<Menu.ItemGroupLabel>{$t("navbar.account")}</Menu.ItemGroupLabel>
					<Menu.Item value="profile">
						<a href="{base}/profile">
						<Menu.ItemText>{$t("navbar.profile")}</Menu.ItemText>
						</a>
					</Menu.Item>
					<Menu.Item value="appointments">
						<a href="{base}/appointments">
						<Menu.ItemText>{$t("navbar.appointments")}</Menu.ItemText>
						</a>
					</Menu.Item>
				</Menu.ItemGroup>
				<Menu.Separator />
				<Menu.ItemGroup>
					{#if isProvider}
						<Menu.ItemGroupLabel>Business</Menu.ItemGroupLabel>
						<Menu.Item value="businesses">
							<a href="{base}/my-businesses">
							<Menu.ItemText>{$t("navbar.businesses")}</Menu.ItemText>
							</a>
						</Menu.Item>
					{/if}
					
					<Menu.Item value="create">
						<Menu.ItemText>{$t("navbar.create-businesses")}</Menu.ItemText>
					</Menu.Item>
				</Menu.ItemGroup>
			</Menu.Content>
		</Menu.Positioner>
	</Portal>
</Menu>