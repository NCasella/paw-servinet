<script>
    import {t} from "$lib/i18n/i18n"
</script>

<div class="flex min-h-full flex-col justify-center px-6 py-12 lg:px-8">
  <div class="sm:mx-auto sm:w-full sm:max-w-sm">
    <h2 class="mt-10 text-center text-2xl/9 font-bold tracking-tight text-gray-900">{$t ("login")}</h2>
  </div>

  <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
    <form action="#" method="POST" class="space-y-6">
      <div>
        <label for="email" class="block text-sm/6 font-medium text-gray-900">{$t ("email")}</label>
        <div class="mt-2">
          <input id="email" type="email" name="email" placeholder={$t("input.email")} required autocomplete="email" class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6" />
        </div>
      </div>

      <div>
        <div class="flex items-center justify-between">
          <label for="password" class="block text-sm/6 font-medium text-gray-900">{$t("password")}</label>
        </div>
        <div class="mt-2">
          <input id="password" type="password" placeholder={$t("input.password")} name="password" required autocomplete="current-password" class="block w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6" />
        </div>
      </div>

      <div>
        <div class="flex items-center justify-between">
          <label class="flex items-center space-x-2">
            <input class="checkbox" type="checkbox" checked />
            <p>{$t("login.rememberme")}</p>
          </label>
          <div class="text-sm">
            <a href="#" class="font-semibold text-indigo-600 hover:text-indigo-500">{$t("login.forgotpassword")}</a>
          </div>
        </div>
      </div>
      
      <div>
        <button type="submit" class="flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm/6 font-semibold text-white shadow-xs hover:bg-indigo-500 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600">{$t ("login.submit")}</button>
      </div>
    </form>

    <p class="mt-10 text-center text-sm/6 text-gray-500">
      {$t("login.register-1")}
      <a href="#" class="font-semibold text-indigo-600 hover:text-indigo-500 underline">{$t("login.register-2")}</a>
    </p>
  </div>
</div>
