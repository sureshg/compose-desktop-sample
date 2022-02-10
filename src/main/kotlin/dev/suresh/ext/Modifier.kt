package dev.suresh.ext

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

/** Modifier extensions. */
fun Modifier.outline(error: Boolean): Modifier = composed {
  if (error) border(width = 1.dp, color = Color.Red, shape = RoundedCornerShape(3.dp)) else this
}

inline fun Modifier.ifTrue(
  value: Boolean,
  builder: Modifier.() -> Modifier
) = then(if (value) builder() else Modifier)

inline fun <T : Any> Modifier.ifNotNull(
  value: T?,
  builder: Modifier.(T) -> Modifier
) = then(value?.let { builder(value) } ?: Modifier)

fun main() {
  val mod = Modifier.ifTrue(true) {
    clickable { }
  }.ifNotNull("2") {
    padding(it.toInt().dp)
  }
}
