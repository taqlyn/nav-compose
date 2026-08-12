package com.taqlyn.nav.compose.navigation3

import com.taqlyn.nav.compose.model.DeepLinkNavigator
import com.taqlyn.nav.compose.model.DeferredLink

/**
 * Navigation 3 adapter: maps [DeferredLink] → [NavKeyResult] and replaces [backStack]
 * with the synthetic stack. Guards against double-navigation by [DeferredLink.linkId].
 *
 * Does not depend on androidx.navigation3 deeplink alpha APIs — stack mutation only.
 */
class Nav3DeepLinkNavigator<T>(
    private val backStack: MutableList<T>,
    private val keyMapper: (DeferredLink) -> NavKeyResult<T>?,
) : DeepLinkNavigator {
    private val consumedLinkIds = mutableSetOf<String>()

    override fun navigate(link: DeferredLink): Boolean {
        if (link.linkId in consumedLinkIds) return false
        val result = keyMapper(link) ?: return false
        backStack.clear()
        backStack.addAll(result.syntheticStack)
        consumedLinkIds += link.linkId
        return true
    }

    /** Clears the double-navigation guard (tests / logout). */
    fun clearConsumed() {
        consumedLinkIds.clear()
    }
}
