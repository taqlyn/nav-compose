package com.taqlyn.nav.compose.navigation2

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Builds a Navigation Compose route from path + query params.
 */
object RouteBuilder {
    fun fromPathAndParams(path: String, params: Map<String, String>): String {
        if (params.isEmpty()) return path
        val query =
            params.entries.joinToString("&") { (key, value) ->
                "${encode(key)}=${encode(value)}"
            }
        return if (path.contains('?')) "$path&$query" else "$path?$query"
    }

    private fun encode(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8)
}
