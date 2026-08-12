package com.taqlyn.nav.compose.sample.nav2

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taqlyn.nav.compose.model.DeferredLink
import com.taqlyn.nav.compose.model.MatchType
import com.taqlyn.nav.compose.navigation2.Nav2DeepLinkNavigator
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Nav2SampleApp()
                }
            }
        }
    }
}

@Composable
fun Nav2SampleApp() {
    val navController = rememberNavController()
    val navigator =
        remember {
            Nav2DeepLinkNavigator(
                navControllerProvider = { navController },
                routeMapper = { link ->
                    val sku = link.params["sku"] ?: link.path.trim('/').substringAfterLast('/')
                    if (sku.isBlank()) null else "product/$sku"
                },
            )
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
                linkId = "lnk_sample_nav2",
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

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Status: $status", style = MaterialTheme.typography.bodyMedium)
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                Text("Home", style = MaterialTheme.typography.headlineMedium)
            }
            composable(
                route = "product/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { entry ->
                val id = entry.arguments?.getString("id").orEmpty()
                Text("Product $id", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}
