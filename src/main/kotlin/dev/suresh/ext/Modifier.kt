package dev.suresh.ext

import androidx.compose.desktop.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*

/**
 * Modifier extensions.
 */
fun Modifier.outline(error: Boolean): Modifier = composed {
    when {
        error -> border(
            width = 1.dp,
            color = Color.Red,
            shape = RoundedCornerShape(3.dp)
        )
        else -> this
    }
}

fun main() = Window {
    Column {
        Text("Hello text", modifier = Modifier.outline(true).padding(8.dp))

        Column {
            TabRow(selectedTabIndex = 0, Modifier.height(48.dp)) {
                Tab(
                    selected = true,
                    onClick = { },
                    enabled = true,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text("Tab 1", Modifier.padding(3.dp), textAlign = TextAlign.Center)
                }
                Tab(
                    selected = false,
                    onClick = { },
                    enabled = true,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text("Search: Result", Modifier.padding(3.dp), textAlign = TextAlign.Center)
                }
                Tab(
                    selected = false,
                    onClick = { },
                    enabled = true,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text("Tab 3", Modifier.padding(3.dp), textAlign = TextAlign.Center)
                }
            }
        }
    }
}
