package dev.suresh.ext

import androidx.compose.foundation.*
import androidx.compose.foundation.shape.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

/** Modifier extensions. */
fun Modifier.outline(error: Boolean): Modifier = composed {
    when {
        error -> border(width = 1.dp, color = Color.Red, shape = RoundedCornerShape(3.dp))
        else -> this
    }
}
