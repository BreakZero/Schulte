---
name: kmp-navigation3
description: |
  Compose Multiplatform Navigation 3 for Android / iOS / Desktop / Web KMP projects.
  Use this skill whenever setting up Navigation 3 in a KMP project, defining NavKey routes,
  adding a new screen, creating feature entry builders, wiring NavDisplay in composeApp,
  managing a user-owned backStack, handling cross-feature navigation, configuring
  SavedStateConfiguration / SerializersModule for non-JVM targets, or migrating from
  Compose Navigation / NavController to Navigation 3.

  Trigger on phrases like "KMP navigation", "Compose Multiplatform navigation",
  "Navigation 3", "Nav3", "NavKey", "NavDisplay", "entryProvider",
  "rememberNavBackStack", "SavedStateConfiguration", "SerializersModule",
  "cross-feature navigation", "feature navigation", "add a route",
  "navigate between screens", "back stack", "NavController migration",
  or "NavHost migration".
---

# Compose Multiplatform / KMP Navigation 3

## Principles

- Use Compose Multiplatform Navigation 3 for shared Android / iOS / Desktop / Web UI.
- Use JetBrains `org.jetbrains.androidx.navigation3:navigation3-ui` in `commonMain`.
- Do not use Android-only `androidx.navigation3:navigation3-ui` in shared KMP UI.
- Model every destination as a `@Serializable NavKey`.
- For modular KMP projects, define one sealed route interface per feature.
- Aggregate feature route serializers in `composeApp` using `subclassesOfSealed`.
- Create the back stack with `rememberNavBackStack(savedStateConfiguration, startRoute)`.
- Render navigation with `NavDisplay`.
- Define one `EntryProviderScope` entry builder per feature.
- Wire all feature entries in `composeApp`.
- Navigate inside a feature with `backStack.add(FeatureRoute(...))`.
- Navigate across features through callbacks.
- Pass IDs and scalar parameters through routes; load complex data in the destination.
- Avoid `NavController`, `NavHost`, `NavGraphBuilder`, string routes, and Android reflection-based route serialization in new KMP Nav3 code.

Navigation 3 is closer to state-driven UI:

```kotlin
backStack.add(NoteDetailRoute(noteId))
backStack.removeLastOrNull()
