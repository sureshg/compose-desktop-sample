package dev.suresh.gif

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import org.jetbrains.skija.*
import java.net.*

@Composable
fun GifAnim(url: String, reverse: Boolean = false, modifier: Modifier = Modifier.size(50.dp)) {
  var codec: Codec? by remember(url) { mutableStateOf(null) }

  LaunchedEffect(url) {
    withContext(Dispatchers.IO) {
      println("Loading $url ...")
      val bytes = URL(url).readBytes()
      codec = Codec.makeFromData(Data.makeFromBytes(bytes))
    }
  }

  codec?.let { GifAnim(it, reverse, modifier) }
    ?: Text("Loading...", modifier = modifier)
}

@Composable
fun GifAnim(codec: Codec, reverse: Boolean, modifier: Modifier) {

  val transition = rememberInfiniteTransition()
  val frameIndex by transition.animateValue(
    initialValue = 0,
    targetValue = codec.frameCount - 1,
    Int.VectorConverter,
    animationSpec = infiniteRepeatable(
      animation = keyframes {
        durationMillis = 0
        for ((index, frame) in codec.framesInfo.withIndex()) {
          index at durationMillis
          durationMillis += frame.duration
        }
      },
      repeatMode = when (reverse) {
        true -> RepeatMode.Reverse
        else -> RepeatMode.Restart
      }
    )
  )

  /** Holds [Bitmap] for each frame. */
  val bitmap = remember { Bitmap().apply { allocPixels(codec.imageInfo) } }
  Canvas(modifier) {
    codec.readPixels(bitmap, frameIndex)
    drawImage(bitmap.asImageBitmap())
  }
}
