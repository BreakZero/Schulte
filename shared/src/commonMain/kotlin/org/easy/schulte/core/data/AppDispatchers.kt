package org.easy.schulte.core.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal class AppDispatchers(
  val database: CoroutineDispatcher = Dispatchers.IO,
)
