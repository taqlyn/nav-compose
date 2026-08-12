package com.taqlyn.nav.compose.sample.nav3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taqlyn.nav.compose.model.DeferredLink
import com.taqlyn.nav.compose.model.MatchType
import com.taqlyn.nav.compose.navigation3.Nav3DeepLinkNavigator
import com.taqlyn.nav.compose.navigation3.NavKeyResult
import kotlinx.coroutines.delay

sealed interface NavKey {
    data object Home : NavKey

    data class Product(val id: String, val ref: String? = null) : NavKey
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Nav3SampleApp()
                }
            }
        }
    }
}

@Composable
fun Nav3SampleApp() {
    val backStack = remember { mutableStateListOf<NavKey>(NavKey.Home) }
    val navigator =
        remember {
            Nav3DeepLinkNavigator(backStack) { link ->
                val sku = link.params["sku"] ?: return@Nav3DeepLinkNavigator null
                val product = NavKey.Product(id = sku, ref = link.params["ref"])
                NavKeyResult(
                    target = product,
                    syntheticStack = listOf(NavKey.Home, product),
                )
            }
        }

    var ready by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Waiting for ready gate…") }
    var consumedIds by remember { mutableStateOf(setOf<String>()) }

    val deferredLink =
        remember {
            DeferredLink(
                url = "https://example.com/product/123?ref=invite",
                path = "/product/123",
                params = mapOf("sku" to "123", "ref" to "invite"),
                linkId = "lnk_sample_nav3",
                matchType = MatchType.INSTALL_REFERRER,
                isDeferred = true,
            )
        }

    LaunchedEffect(Unit) {
        delay(300)
        ready = true
        status = "Ready — delivering deferred link"
    }

    LaunchedEffect(ready) {
        if (!ready) return@LaunchedEffect
        val first = navigator.navigate(deferredLink)
        if (first) {
            consumedIds = consumedIds + deferredLink.linkId
            status = "Navigated once (linkId=${deferredLink.linkId})"
        }
        delay(200)
        val second = navigator.navigate(deferredLink)
        status =
            if (!second) {
                "Double-nav blocked for ${deferredLink.linkId} (consumed=${deferredLink.linkId in consumedIds})"
            } else {
                "Unexpected second navigation"
            }
    }

    val current = backStack.lastOrNull() ?: NavKey.Home

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Status: $status", style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "Stack: ${backStack.joinToString(" → ")}",
            style = MaterialTheme.typography.bodySmall,
        )
        when (val key = current) {
            is NavKey.Home -> Text("Home", style = MaterialTheme.typography.headlineMedium)
            is NavKey.Product ->
                Text(
                    "Product ${key.id} (ref=${key.ref})",
                    style = MaterialTheme.typography.headlineMedium,
                )
        }
    }
}
