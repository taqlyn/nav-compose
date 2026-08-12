package com.taqlyn.nav.compose.navigation2

import androidx.navigation.NavController
import com.taqlyn.nav.compose.model.DeepLinkNavigator
import com.taqlyn.nav.compose.model.DeferredLink

/**
 * Navigation Compose 2 adapter: maps [DeferredLink] → route string and
 * calls [NavController.navigate]. Guards against double-navigation by [DeferredLink.linkId].
 */
class Nav2DeepLinkNavigator(
    private val navControllerProvider: () -> NavController,
    private val routeMapper: (DeferredLink) -> String?,
) : DeepLinkNavigator {
    private val consumedLinkIds = mutableSetOf<String>()

    override fun navigate(link: DeferredLink): Boolean {
        if (link.linkId in consumedLinkIds) return false
        val route = routeMapper(link) ?: return false
        navControllerProvider().navigate(route)
        consumedLinkIds += link.linkId
        return true
    }

    /** Clears the double-navigation guard (tests / logout). */
    fun clearConsumed() {
        consumedLinkIds.clear()
    }
}
