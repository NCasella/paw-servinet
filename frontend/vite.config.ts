import tailwindcss from '@tailwindcss/vite';
import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';
import Icons from 'unplugin-icons/vite'

export default defineConfig({
	plugins: [
		tailwindcss(),
		sveltekit(),
		Icons({ compiler: 'svelte' })
	],
	test: {
		globals: true,
		environment: 'jsdom',
		include: ['tests/**/*.{test,spec}.{ts,js}']
	}
});
