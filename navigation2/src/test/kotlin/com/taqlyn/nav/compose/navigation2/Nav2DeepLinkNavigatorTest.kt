package com.taqlyn.nav.compose.navigation2

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.google.common.truth.Truth.assertThat
import com.taqlyn.nav.compose.model.DeferredLink
import com.taqlyn.nav.compose.model.MatchType
import org.junit.Test
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class Nav2DeepLinkNavigatorTest {
    private val link =
        DeferredLink(
            url = "https://example.com/product/123?ref=invite",
            path = "/product/123",
            params = mapOf("ref" to "invite", "sku" to "123"),
            linkId = "lnk_nav2_1",
            matchType = MatchType.INSTALL_REFERRER,
            isDeferred = true,
        )

    @Test
    fun navigate_firstCall_returnsTrueAndNavigates() {
        val navController = mock<NavController>()
        val navigator =
            Nav2DeepLinkNavigator(
                navControllerProvider = { navController },
                routeMapper = {
                    RouteBuilder.fromPathAndParams("product/${it.params["sku"]}", it.params - "sku")
                },
            )

        val first = navigator.navigate(link)

        assertThat(first).isTrue()
        // Kotlin default args compile to navigate(route, navOptions, navigatorExtras)
        verify(navController, times(1)).navigate(
            eq("product/123?ref=invite"),
            isNull<NavOptions>(),
            isNull<Navigator.Extras>(),
        )
    }

    @Test
    fun navigate_secondSameLinkId_returnsFalseWithoutNavigating() {
        val navController = mock<NavController>()
        val navigator =
            Nav2DeepLinkNavigator(
                navControllerProvider = { navController },
                routeMapper = { "product/${it.params["sku"]}" },
            )

        assertThat(navigator.navigate(link)).isTrue()
        assertThat(navigator.navigate(link.copy(url = "https://other"))).isFalse()

        verify(navController, times(1)).navigate(
            eq("product/123"),
            isNull<NavOptions>(),
            isNull<Navigator.Extras>(),
        )
    }

    @Test
    fun navigate_nullMapper_returnsFalse() {
        val navController = mock<NavController>()
        val navigator =
            Nav2DeepLinkNavigator(
                navControllerProvider = { navController },
                routeMapper = { null },
            )

        assertThat(navigator.navigate(link)).isFalse()
        verify(navController, never()).navigate(
            org.mockito.kotlin.any<String>(),
            anyOrNull(),
            anyOrNull(),
        )
    }

    @Test
    fun clearConsumed_allowsNavigateAgain() {
        val navController = mock<NavController>()
        val navigator =
            Nav2DeepLinkNavigator(
                navControllerProvider = { navController },
                routeMapper = { "home" },
            )

        assertThat(navigator.navigate(link)).isTrue()
        navigator.clearConsumed()
        assertThat(navigator.navigate(link)).isTrue()
        verify(navController, times(2)).navigate(
            eq("home"),
            isNull<NavOptions>(),
            isNull<Navigator.Extras>(),
        )
    }

    @Test
    fun routeBuilder_encodesQueryParams() {
        val route =
            RouteBuilder.fromPathAndParams(
                "/product/123",
                mapOf("ref" to "invite team", "sku" to "123"),
            )
        assertThat(route).isEqualTo("/product/123?ref=invite+team&sku=123")
    }
}
