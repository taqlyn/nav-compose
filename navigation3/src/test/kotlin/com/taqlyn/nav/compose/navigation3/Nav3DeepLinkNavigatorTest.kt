package com.taqlyn.nav.compose.navigation3

import com.google.common.truth.Truth.assertThat
import com.taqlyn.nav.compose.model.DeferredLink
import com.taqlyn.nav.compose.model.MatchType
import org.junit.Test

class Nav3DeepLinkNavigatorTest {
    private sealed interface Key {
        data object Home : Key

        data class Product(val id: String) : Key
    }

    private val link =
        DeferredLink(
            url = "https://example.com/product/123?ref=invite",
            path = "/product/123",
            params = mapOf("ref" to "invite", "sku" to "123"),
            linkId = "lnk_nav3_1",
            matchType = MatchType.INSTALL_REFERRER,
            isDeferred = true,
        )

    @Test
    fun navigate_replacesBackStackWithSyntheticStack() {
        val backStack = mutableListOf<Key>(Key.Home)
        val navigator =
            Nav3DeepLinkNavigator(backStack) { deferred ->
                val id = deferred.params["sku"] ?: return@Nav3DeepLinkNavigator null
                val product = Key.Product(id)
                NavKeyResult(target = product, syntheticStack = listOf(Key.Home, product))
            }

        assertThat(navigator.navigate(link)).isTrue()
        assertThat(backStack).containsExactly(Key.Home, Key.Product("123")).inOrder()
    }

    @Test
    fun navigate_secondSameLinkId_returnsFalseWithoutMutatingStack() {
        val backStack = mutableListOf<Key>(Key.Home)
        val navigator =
            Nav3DeepLinkNavigator(backStack) { deferred ->
                val product = Key.Product(deferred.params.getValue("sku"))
                NavKeyResult(target = product, syntheticStack = listOf(product))
            }

        assertThat(navigator.navigate(link)).isTrue()
        assertThat(backStack).containsExactly(Key.Product("123"))

        assertThat(navigator.navigate(link)).isFalse()
        assertThat(backStack).containsExactly(Key.Product("123"))
    }

    @Test
    fun navigate_nullMapper_returnsFalse() {
        val backStack = mutableListOf<Key>(Key.Home)
        val navigator = Nav3DeepLinkNavigator<Key>(backStack) { null }

        assertThat(navigator.navigate(link)).isFalse()
        assertThat(backStack).containsExactly(Key.Home)
    }

    @Test
    fun clearConsumed_allowsNavigateAgain() {
        val backStack = mutableListOf<Key>(Key.Home)
        val navigator =
            Nav3DeepLinkNavigator(backStack) { deferred ->
                val product = Key.Product(deferred.params.getValue("sku"))
                NavKeyResult(target = product, syntheticStack = listOf(Key.Home, product))
            }

        assertThat(navigator.navigate(link)).isTrue()
        navigator.clearConsumed()
        backStack.clear()
        backStack += Key.Home
        assertThat(navigator.navigate(link)).isTrue()
        assertThat(backStack).containsExactly(Key.Home, Key.Product("123")).inOrder()
    }
}
