package com.taqlyn.nav.compose.navigation3

/**
 * Result of mapping a DeferredLink to a Navigation 3 key + synthetic back stack.
 *
 * Nav3 has no deeplink DSL; apps replace the owned back stack with [syntheticStack]
 * so Up/Back matches manual navigation into [target].
 */
data class NavKeyResult<T>(
    val target: T,
    val syntheticStack: List<T>,
)
