@file:Suppress("FunctionName")

package dev.suresh.gif

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.*
import kotlinx.coroutines.*
import org.jetbrains.skija.*
import org.jetbrains.skija.Canvas

@Composable
fun GifAnim(modifier: Modifier, codec: Codec) {
  val animation = remember(codec) { GifAnim(codec) }
  LaunchedEffect(animation) {
    while (isActive) {
      withFrameNanos { animation.update(it) }
    }
  }

  Canvas(
    modifier.pointerInput(Unit) {
      detectTapGestures(
        onTap = {
          println("Clicked on $it")
        }
      )
    }
  ) {
    scale(scale = 0.5f) {
      rotate(0f) {
        translate(5f, 5f) {
          drawIntoCanvas {
            it.nativeCanvas.drawString(
              "Animated Gif",
              0f,
              0f,
              Font().apply { size = 30f },
              Paint().asFrameworkPaint()
            )
            animation.draw(it.nativeCanvas)
          }
        }
      }
    }
  }
}

class GifAnim(private val codec: Codec) {
  /** Holds [Bitmap] for each frame. */
  private val bitmap = Bitmap().apply { allocPixels(codec.imageInfo) }

  /** Animation duration in nanos */
  private val animFrameDurations = codec.framesInfo.map { it.duration * 1_000_000L }

  /** Total animation duration in nano seconds. */
  private val animDuration = animFrameDurations.sum()

  private var startTime = -1L
  private var lastFrame = 0
  private var lastDuration = 0L

  private var currFrame by mutableStateOf(0)

  /** Updates the current frame to display on each frame dispatch. */
  fun update(nanoTime: Long) {
    if (startTime == -1L) startTime = nanoTime
    currFrame = frameOf(time = (nanoTime - startTime) % animDuration)
  }

  /** Find the next frame after the given time in animation frames. */
  private fun frameOf(time: Long): Int {
    var t = lastDuration
    for (frame in lastFrame until animFrameDurations.size) {
      if (t >= time) {
        lastFrame = frame
        lastDuration = t
        return frame
      }
      t += animFrameDurations[frame]
    }
    lastFrame = 0
    lastDuration = 0L
    return 0
  }

  /** Reads [currFrame] into [bitmap] and draw into the [canvas]. */
  fun draw(canvas: Canvas) {
    codec.readPixels(bitmap, currFrame)
    canvas.drawImage(Image.makeFromBitmap(bitmap), 0f, 0f)
  }
}
