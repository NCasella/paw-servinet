<script lang="ts">
    import {t} from "$lib/i18n/i18n"
    import {loginWithBasicAuth} from "$services/authenticate"
	import { getCurrentUser } from "$services/userService";

    let username = '';
    let password = '';
    let errorMessage = '';
    let loading = false;

    async function handleSubmit(event: Event) {
        event.preventDefault(); // evita submit HTTP automático

        loading = true;
        errorMessage = '';

        const ok = await loginWithBasicAuth(username, password);

        loading = false;

        if (ok) {
          getCurrentUser().then( () => history.back())   
        }
        errorMessage = "Invalid username or password";
    
  }
</script>

<div class="flex min-h-full flex-col justify-center px-6 py-12 lg:px-8">
  <div class="sm:mx-auto sm:w-full sm:max-w-sm">
    <h2 class="mt-10 text-center text-2xl/9 font-bold tracking-tight">{$t ("login")}</h2>
  </div>

  <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
    <form on:submit|preventDefault={handleSubmit} class="space-y-6">
      <div>
        <label for="email" class="block text-sm/6 font-medium ">{$t ("email")}</label>
        <div class="mt-2">
          <input id="email" bind:value={username} type="email" name="email" placeholder={$t("input.email")} required autocomplete="email" class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6" />
        </div>
      </div>

      <div>
        <div class="flex items-center justify-between">
          <label for="password" class="block text-sm/6 font-medium">{$t("password")}</label>
        </div>
        <div class="mt-2">
          <input id="password" bind:value={password} type="password" placeholder={$t("input.password")} name="password" required autocomplete="current-password" class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6" />
        </div>
      </div>

      <div>
        <div class="flex items-center justify-between">
          <label class="flex items-center space-x-2">
            <input class="checkbox" type="checkbox" checked />
            <p>{$t("login.rememberme")}</p>
          </label>
          <div class="text-sm">
            <a href="#" class="font-semibold text-primary-400">{$t("login.forgotpassword")}</a>
          </div>
        </div>
      </div>
      
      <div>
        <button type="submit" disabled={loading} class="flex w-full justify-center rounded-md bg-primary-500 px-3 py-1.5 text-sm/6 font-semibold text-white shadow-xs hover:bg-indigo-500 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600">{$t ("login.submit")}</button>
      </div>
    </form>


    <p class="mt-10 text-center text-sm/6 text-surface">
      {$t("login.register-1")}
      <a href="#" class="font-semibold text-primary-400 underline">{$t("login.register-2")}</a>
    </p>
  </div>

  {#if errorMessage}
    <p class="error">{errorMessage}</p>
  {/if}
</div>
