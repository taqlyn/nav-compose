package com.taqlyn.nav.compose.model

/**
 * Canonical resolve payload for nav adapters — mirrors packages/sdk-contract
 * and SdkCore DeferredLink. Map field-by-field from SdkCore; do not depend on
 * sdk-android from this package.
 */
data class DeferredLink(
    val url: String,
    val path: String,
    val params: Map<String, String> = emptyMap(),
    val linkId: String,
    val matchType: MatchType,
    val isDeferred: Boolean,
    val campaign: Campaign? = null,
)

/** How the deferred / warm link was matched. */
enum class MatchType {
    INSTALL_REFERRER,
    CLIPBOARD,
    APP_CLIP,
    CLAIM,
    NONE,
    ;

    fun toWire(): String =
        when (this) {
            INSTALL_REFERRER -> "install_referrer"
            CLIPBOARD -> "clipboard"
            APP_CLIP -> "app_clip"
            CLAIM -> "claim"
            NONE -> "none"
        }

    companion object {
        fun fromWire(value: String?): MatchType =
            when (value) {
                "install_referrer" -> INSTALL_REFERRER
                "clipboard" -> CLIPBOARD
                "app_clip" -> APP_CLIP
                "claim" -> CLAIM
                "none" -> NONE
                else -> NONE
            }
    }
}

/** Optional UTM / campaign attribution. */
data class Campaign(
    val values: Map<String, String> = emptyMap(),
) {
    val utmSource: String? get() = values["utm_source"]
    val utmCampaign: String? get() = values["utm_campaign"]
}
