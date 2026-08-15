import en from "../locales/en.json";
import tr from "../locales/tr.json";
import config from "../config.json";

const dictionaries = { en, tr };
const SUPPORTED = new Set(config.supportedLanguages ?? ["en", "tr"]);

// nested objeden "a.b.c" yolunu al
function getByPath(obj, path) {
  return path.split(".").reduce((acc, key) => (acc && acc[key] !== undefined ? acc[key] : undefined), obj);
}

// opsiyonel: basit placeholder değişimi (örn: "Hello, {name}")
function interpolate(str, params) {
  if (!params || typeof str !== "string") return str;
  return str.replace(/\{(\w+)\}/g, (_, k) => (params[k] ?? `{${k}}`));
}

/**
 * translate(lang, key, params?)
 *  - key "navBar.title" gibi nokta gösterimini destekler
 *  - lang desteklenmiyorsa EN'e düşer
 *  - key bulunamazsa EN'de dener, o da yoksa key'i döner
 *  - params: {name:"Berkay"} gibi placeholder doldurma
 */
export function translate(lang, key, params) {
  const safeLang = SUPPORTED.has(lang) ? lang : "en";
  const dict = dictionaries[safeLang] || dictionaries["en"];

  let val = getByPath(dict, key);
  if (val === undefined) {
    const fallback = getByPath(dictionaries["en"], key);
    val = fallback !== undefined ? fallback : key; // son çare: key’in kendisi
  }

  return interpolate(val, params);
}
