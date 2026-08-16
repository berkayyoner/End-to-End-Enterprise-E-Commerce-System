package com.berkay.common.i18n;

/**
 * Default locale for the whole platform (public storefront, personnel panel, and every
 * translation table). RULES.md requires Turkish by default with English (and future
 * languages) as additions - adding a language is a data change (new locale_code rows),
 * never a code change, so no enum of "supported" codes exists here.
 */
public final class LocaleCodes {

	public static final String DEFAULT_LOCALE = "tr";

	private LocaleCodes() {
	}
}
