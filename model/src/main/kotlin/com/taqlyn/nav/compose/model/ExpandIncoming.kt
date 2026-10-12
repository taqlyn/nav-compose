package com.taqlyn.nav.compose.model

/**
 * Gap 1 helper — expand a warm short URL before navigation.
 *
 * Inject SdkCore.resolveIncomingUrl. Returns [fallbackPath] on failure.
 * Nav packages stay free of Match / HTTP.
 */
suspend fun expandIncomingRoute(
    url: String,
    resolveIncomingUrl: suspend (String) -> ResolvedHref?,
    fallbackPath: String = "/",
): String {
    return try {
        resolveIncomingUrl(url)?.href?.takeIf { it.isNotBlank() } ?: fallbackPath
    } catch (_: Exception) {
        fallbackPath
    }
}

/** Minimal href carrier so this module does not depend on sdk-android types. */
data class ResolvedHref(
    val href: String,
    val linkId: String? = null,
    val pendingExpand: Boolean = false,
)
