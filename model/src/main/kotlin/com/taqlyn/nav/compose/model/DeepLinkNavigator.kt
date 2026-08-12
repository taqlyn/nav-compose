package com.taqlyn.nav.compose.model

/**
 * Navigation handoff for a resolved [DeferredLink].
 * Returns true when navigation was performed; false when skipped
 * (unmapped route or already navigated for this [DeferredLink.linkId]).
 */
fun interface DeepLinkNavigator {
    fun navigate(link: DeferredLink): Boolean
}
