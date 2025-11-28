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
			pages: '../webapp/app',
			assets: '../webapp/app',
			fallback: 'index.html'
		}),
		paths: {
			base: '/webapp_war_exploded/app', // Ruta base de tu aplicación
			relative: true,	
		},
		alias: {
			$utils: 'src/utils',
			$services: 'src/services',
			$stores: 'src/stores',
		}
  },
};

export default config;
