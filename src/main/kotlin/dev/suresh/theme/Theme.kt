package dev.suresh.theme

import androidx.compose.material.*
import androidx.compose.runtime.*

@Composable
fun MyTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    val colors = when (darkTheme) {
        true -> DarkColorPalette
        else -> LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
