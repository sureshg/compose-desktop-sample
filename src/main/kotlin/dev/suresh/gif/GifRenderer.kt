package dev.suresh.gif

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.*
import kotlinx.coroutines.*
import org.jetbrains.skija.*
import java.net.*
import java.util.*

@Composable
fun GifRenderer(url: String, reverse: Boolean = false): State<Painter> {

  // https://css-tricks.com/snippets/html/base64-encode-of-1x1px-transparent-gif/
  val blankGif = Base64.getDecoder().decode("R0lGODlhAQABAHAAACH5BAUAAAAALAAAAAABAAEAAAICRAEAOw==")
  val blankGifCodec = Codec.makeFromData(Data.makeFromBytes(blankGif))

  // Load the Gif
  val codec by produceState(blankGifCodec) {
    withContext(Dispatchers.IO) {
      val bytes = URL(url).readBytes()
      value = Codec.makeFromData(Data.makeFromBytes(bytes))
    }
  }

  val images = remember(codec) {
    (0 until codec.frameCount).map { frameIndex ->
      val bitmap = Bitmap()
      bitmap.allocPixels(codec.imageInfo)
      codec.readPixels(bitmap, frameIndex)
      BitmapPainter(bitmap.asImageBitmap())
    }
  }

  val transition = rememberInfiniteTransition()

  val frameIndex by transition.animateValue(
    initialValue = 0,
    targetValue = codec.frameCount - 1,
    Int.VectorConverter,
    animationSpec = infiniteRepeatable(
      animation = keyframes {
        durationMillis = 0
        for ((index, frame) in codec.framesInfo.withIndex()) {
          // Add keyframes
          index at durationMillis with LinearEasing
          // For handling blank gif
          val frameDuration = frame.duration.takeIf { it > 0 } ?: 1
          durationMillis += frameDuration
        }
      },
      repeatMode = when (reverse) {
        true -> RepeatMode.Reverse
        else -> RepeatMode.Restart
      }
    )
  )

  return remember(images) { derivedStateOf { images[frameIndex] } }
}
