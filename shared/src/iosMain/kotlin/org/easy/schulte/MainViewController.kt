package org.easy.schulte

import androidx.compose.ui.window.ComposeUIViewController
import org.easy.schulte.core.data.IosDatabaseDriverFactory

@Suppress("FunctionName")
fun MainViewController() = ComposeUIViewController { App(IosDatabaseDriverFactory()) }
