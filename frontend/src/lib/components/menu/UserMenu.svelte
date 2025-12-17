<script lang="ts">
	import { Menu, Portal } from '@skeletonlabs/skeleton-svelte';
    import { t } from "$lib/i18n/i18n";
	import { Avatar } from '@skeletonlabs/skeleton-svelte';
	import { base } from '$app/paths';
	import ImageWithFallback from '$lib/components/global/ImageWithFallback.svelte';
	import { UserContactInfo } from '$models/ContactInfo';
	import { closeSession, currentUserIsProvider } from '$services/userService';
	import { invalidateAll } from '$app/navigation';
	import { navTo } from '$lib/navigation/pageInfo';
	import { User } from '$models/User';
	import UserDefaultImg from "$lib/images/profile_default.png"

	let {currentUser} = $props()

	async function logUserOut () {
        closeSession()
        await invalidateAll(); 
        navTo("/login") 
    }
</script>

<Menu>
	<Menu.Trigger class="btn" >
		<Avatar class="h-12 w-12">
				<Avatar.Image class="w-full h-full" src={currentUser.getProfilePictureSrc()} alt="base" />
				<Avatar.Fallback><img class="rounded-2xl" src={UserDefaultImg} alt="pic"/></Avatar.Fallback>
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
					<Menu.Item value="close session" class="btn  bg-error-400 text-error-700">
						 <!-- Logout -->
						  <Menu.ItemText onclick={logUserOut}>
							{$t('profile.logout')}
						  </Menu.ItemText>
					</Menu.Item>
				</Menu.ItemGroup>
				<Menu.Separator />
				<Menu.ItemGroup>
					{#if currentUser.isProvider}
						<Menu.ItemGroupLabel>Business</Menu.ItemGroupLabel>
						<Menu.Item value="businesses">
							<a href="{base}/my-businesses">
							<Menu.ItemText>{$t("navbar.businesses")}</Menu.ItemText>
							</a>
						</Menu.Item>
					{/if}
					
					<Menu.Item value="create">
						<a href="{base}/create-business">
						<Menu.ItemText>{$t("navbar.create-businesses")}</Menu.ItemText>
						</a>
					</Menu.Item>
				</Menu.ItemGroup>
			</Menu.Content>
		</Menu.Positioner>
	</Portal>
</Menu>
        