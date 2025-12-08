<script lang="ts">
	import { Menu, Portal } from '@skeletonlabs/skeleton-svelte';
    import { t } from "$lib/i18n/i18n";
	import { Avatar } from '@skeletonlabs/skeleton-svelte';
	import { base } from '$app/paths';

	let {user} = $props()
	let isProvider = user.isProvider 
</script>

<Menu>
	<Menu.Trigger class="btn preset-filled">
		<Avatar>
				<Avatar.Image src="{user?.profilePicture}" alt="base" />
				<Avatar.Fallback>SK</Avatar.Fallback>
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
							<a href="{base}/businesses">
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