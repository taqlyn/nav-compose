# Taqlyn Navigation Compose adapters (`nav-compose`)

Optional Jetpack Compose navigation helpers for Taqlyn deferred / warm links.
**Version independently from SdkCore** — apps that do not use Compose Navigation
need not ship these artifacts.

## Modules / artifacts

| Module | Artifact role |
|--------|----------------|
| `:model` | Shared `DeferredLink` + `DeepLinkNavigator` (no Navigation / Play Referrer) |
| `:navigation2` | `Nav2DeepLinkNavigator` for Navigation Compose 2.x |
| `:navigation3` | `Nav3DeepLinkNavigator` for Nav3-style owned back stacks |
| `:sample-nav2` | Proof app — ready → navigate once → consume |
| `:sample-nav3` | Proof app — synthetic stack + double-nav guard |

## Mapping SdkCore → nav `DeferredLink`

Nav packages **do not** depend on `sdk-android`. Copy fields from SdkCore:

```kotlin
fun com.taqlyn.sdk.DeferredLink.toNav(): com.taqlyn.nav.compose.model.DeferredLink =
    com.taqlyn.nav.compose.model.DeferredLink(
        url = url,
        path = path,
        params = params,
        linkId = linkId,
        matchType = com.taqlyn.nav.compose.model.MatchType.fromWire(matchType.toWire()),
        isDeferred = isDeferred,
        campaign = campaign?.let {
            com.taqlyn.nav.compose.model.Campaign(it.values)
        },
    )
```

Shape (mirrors `packages/sdk-contract`): `url`, `path`, `params`, `linkId`,
`matchType`, `isDeferred`, optional `campaign`.

## Ready → navigate once → consume

1. Hold the pending `DeferredLink` until the nav host / graph is ready
   (`setReadyForNavigation(true)` on SdkCore, or a local ready flag in samples).
2. Call `navigator.navigate(link)` **once**.
3. Mark consumed (`SdkCore.consume(linkId)` or a local flag). Navigators also
   guard by `linkId` so a second `navigate` with the same id returns `false`.
4. On logout / tests, call `clearConsumed()` on the navigator if you need to
   allow the same id again.

```kotlin
// Nav2
val navigator = Nav2DeepLinkNavigator(
    navControllerProvider = { navController },
    routeMapper = { link -> "product/${link.params["sku"]}" },
)
if (navigator.navigate(link)) SdkCore.consume(link.linkId)

// Nav3 — replace owned back stack with a synthetic stack (no deeplink DSL)
val navigator = Nav3DeepLinkNavigator(backStack) { link ->
    val product = Product(link.params.getValue("sku"))
    NavKeyResult(target = product, syntheticStack = listOf(Home, product))
}
```

## Dependencies (what this package does **not** include)

- **No** `com.android.installreferrer` / Play Install Referrer
- **No** pasteboard / clipboard kits
- **No** HTTP resolve client
- **No** hard dependency on `sdk-android`

Feature / nav code imports these adapters only; Install Referrer stays inside SdkCore adapters.

## Build / verify

From `packages/nav-compose`:

```bash
./gradlew :navigation2:test :navigation3:test
./gradlew :sample-nav2:assembleDebug :sample-nav3:assembleDebug
./gradlew :navigation2:dependencies | grep -i installreferrer || true
```
