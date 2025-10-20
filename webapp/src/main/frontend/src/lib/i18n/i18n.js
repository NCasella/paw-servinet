import { writable, derived } from "svelte/store";

import en from "./messages-en";
import es from "./messages-es";

export const translations = {
  en,
  es,
};

export const locales = Object.keys(translations);

export const locale = writable("en");

function translate(locale, key, vars) {
  if (!key) throw new Error("no key provided to $t()");
  if (!locale) throw new Error(`no translation for key "${key}"`);

  const dictionary = translations[locale];
  if (!dictionary) throw new Error(`no translation for locale "${locale}"`);

  let text = dictionary[key];
  if (!text) throw new Error(`no translation found for ${locale}.${key}`);

  // Replace variables
  Object.keys(vars).forEach((k) => {
    text = text.replace(new RegExp(`{{${k}}}`, "g"), vars[k]);
  });

  return text;
}

export const t = derived(locale, ($locale) => (key, vars = {}) =>
  translate($locale, key, vars)
);
