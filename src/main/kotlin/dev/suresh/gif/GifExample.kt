@file:Suppress("FunctionName")

package dev.suresh.gif

import androidx.compose.desktop.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.*
import org.jetbrains.skija.*

fun main() =
    Window(
        title = "Animated Gif Example",
        centered = true,
    ) {
      val gifData = remember {
        val bytes = readResource("gif/particles.gif")
        Codec.makeFromData(Data.makeFromBytes(bytes))
      }
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        GifAnimation(gifData = gifData)
      }
    }

@Composable
fun GifAnimation(modifier: Modifier = Modifier.size(100.dp), gifData: Codec) {
  Column {
    Text(
        modifier =
            Modifier.drawBehind {
              drawCircle(androidx.compose.ui.graphics.Color.Cyan, radius = 30f)
            },
        text = gifData.encodedImageFormat.toString())

    TextLine.make("", Font()).use { it.width }

    Spacer(modifier = Modifier.size(10.dp))

    Canvas(modifier) {
      rotate(45f) {
        translate(10f, 10f) {
          drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawString(
                "Hello Canvas",
                center.x,
                center.y,
                Font().apply { size = 50f },
                Paint().asFrameworkPaint())
          }
        }
      }
    }
  }
}

fun readResource(path: String) =
    Thread.currentThread().contextClassLoader.getResourceAsStream(path)?.readBytes()
