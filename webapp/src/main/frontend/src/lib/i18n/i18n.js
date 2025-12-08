import { writable, derived } from "svelte/store";

import en from "./messages-en";
import es from "./messages-es";

export const translations = {
  en,
  es,
};

export const locales = Object.keys(translations);

export const locale = writable( getNavLanguage() );

function translate(locale, key, vars) {
  if (!key) throw new Error("no key provided to $t()");
  if (!locale) throw new Error(`no translation for key "${key}"`);

  const dictionary = translations[locale];
  let text = dictionary[key];

  if (!text) throw new Error(`no translation found for ${locale}.${key}`);

  // Soporte para variables posicionales tipo {0}
  if (Array.isArray(vars)) {
    vars.forEach((value, index) => {
      text = text.replace(new RegExp(`\\{${index}\\}`, "g"), value);
    });
  } //else if (typeof vars === "object") {
  //  // Soporte para {{variable}}
  //  Object.keys(vars).forEach((k) => {
  //    text = text.replace(new RegExp(`{{${k}}}`, "g"), vars[k]);
  //  });
  //}

  return text;
}

export function setLanguage(lang) {
  locale.set(lang)
}

export function resetLanguage() {
  setLanguage( getNavLanguage())
}

export const t = derived(locale, ($locale) => (key, vars = {}) =>
  translate($locale, key, vars)
);

const DEFAULT_LANG = "en"

function getNavLanguage() {
  var userLang = navigator.language || navigator.userLanguage
  return( userLang=="es" || userLang=="en")? userLang : DEFAULT_LANG
}

