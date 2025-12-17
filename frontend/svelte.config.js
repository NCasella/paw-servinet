import adapter from '@sveltejs/adapter-static';
import { vitePreprocess } from '@sveltejs/vite-plugin-svelte';
import sveltePreprocess from 'svelte-preprocess';

/** @type {import('@sveltejs/kit').Config} */
const config = {
	// Consult https://svelte.dev/docs/kit/integrations
	// for more information about preprocessors
	preprocess: [
		vitePreprocess(),
		sveltePreprocess({
		scss: {
			includePaths: ['src'],   // opcional
		}
		})
	],

	kit: {
		adapter: adapter({
			pages: 'build',
			assets: 'build',
			fallback: 'index.html'
		}),
		paths: {
			base: '/paw-2024a-04', // Ruta base de tu aplicación
			relative: false,	
		},
		alias: {
			$utils: 'src/utils',
			$services: 'src/services',
			$stores: 'src/stores',
			$models: 'src/models',
			$icons: 'src/lib/components/global/Icon.svelte',
			$i18: 'src/lib/i18n/i18n.js'
		}
  },
};

export default config;
