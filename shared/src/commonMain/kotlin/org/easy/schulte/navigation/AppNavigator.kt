package org.easy.schulte.navigation

import androidx.navigation3.runtime.NavKey

class AppNavigator(
  private val backStack: MutableList<NavKey>,
) {
  fun navigate(route: AppRoute) {
    if (backStack.lastOrNull() != route) backStack.add(route)
  }

  fun replaceTop(route: AppRoute) {
    if (backStack.isNotEmpty()) backStack.removeLast()
    backStack.add(route)
  }

  fun resetTo(route: AppRoute) {
    backStack.clear()
    backStack.add(StartRoute)
    if (route != StartRoute) backStack.add(route)
  }

  fun navigateUp() {
    if (backStack.size > 1) backStack.removeLast()
  }

  fun onBack() {
    when (backStack.lastOrNull()) {
      TrainingRoute,
      ReportRoute,
      -> resetTo(ConfigRoute)

      else -> navigateUp()
    }
  }
}

val StartRoute: AppRoute = ConfigRoute
