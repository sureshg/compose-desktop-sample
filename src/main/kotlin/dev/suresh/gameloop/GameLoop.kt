package dev.suresh.gameloop

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.desktop.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import compose.icons.*
import compose.icons.simpleicons.*
import dev.suresh.gif.*
import dev.suresh.jfr.*
import kotlinx.coroutines.*
import java.io.*
import javax.swing.*

val jfrEvent = FrameRate(0)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FrameRate() {
  var frameRate by remember { mutableStateOf(0) }

  LaunchedEffect(Unit) {
    var frameCount = 0
    var prevTime = withFrameNanos { it }
    while (isActive) {
      withFrameNanos {
        frameCount++
        val seconds = (it - prevTime) / 1E9 // 1E9 nanoseconds is 1 second
        if (seconds >= 1) {
          frameRate = (frameCount / seconds).toInt()
          prevTime = it
          frameCount = 0
          jfrEvent.fps = frameRate
        }
      }
    }
  }

  var selected by remember { mutableStateOf(false) }
  val corner by animateDpAsState(if (selected) 10.dp else 0.dp)
  val color by animateColorAsState(if (selected) Color.Red else Color.Green)

  Column(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopCenter)) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
      Icon(
        imageVector = SimpleIcons.Kotlin,
        contentDescription = "",
        tint = MaterialTheme.colors.primary,
        modifier = Modifier.align(Alignment.CenterVertically)
      )
      Text(
        "Frame rate: $frameRate fps",
        modifier =
        Modifier.border(1.dp, color, RoundedCornerShape(corner))
          .background(Color.Yellow)
          .padding(12.dp)
          .pointerMoveFilter(
            onEnter = {
              selected = true
              true
            },
            onExit = {
              selected = false
              true
            }
          ),
        color = Color.Red
      )
    }
  }

  Box {
    val state = rememberLazyListState()
    LazyColumn(
      modifier = Modifier.padding(10.dp),
      state = state,
      reverseLayout = false,
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      items((1..100).toList()) { x ->
        Text(x.toString(), modifier = Modifier.graphicsLayer(alpha = 0.5f).height(20.dp))
      }
    }

    Spacer(Modifier.padding(horizontal = 30.dp))
    VerticalScrollbar(
      adapter = rememberScrollbarAdapter(state),
      modifier = Modifier.align(Alignment.Center),
    )
  }
}

/** Modifier extensions. */
fun Modifier.outline(error: Boolean): Modifier = composed {
  when {
    error -> border(width = 1.dp, color = Color.Red, shape = RoundedCornerShape(3.dp))
    else -> this
  }
}

@Composable
fun ScreenShot() {
  var save by remember { mutableStateOf(false) }
  Button(
    onClick = {
      save = true
    }
  ) {
    Text("Screenshot")
  }

  if (save) {
    SwingUtilities.invokeLater {
      println("Saving the image!")
      // val appWindow = LocalAppWindow.current
      val window = TestComposeWindow(width = 1024, height = 768)
      window.setContent {
        Column {
          Text("Hello text", modifier = Modifier.outline(true).padding(8.dp))

          Column {
            TabRow(selectedTabIndex = 0, Modifier.height(48.dp)) {
              Tab(
                selected = true,
                onClick = {},
                enabled = true,
                modifier = Modifier.fillMaxHeight()
              ) {
                Text("Tab 1", Modifier.padding(3.dp), textAlign = TextAlign.Center)
              }
              Tab(
                selected = false,
                onClick = {},
                enabled = true,
                modifier = Modifier.fillMaxHeight()
              ) {
                Text(
                  "Search: Result",
                  Modifier.padding(3.dp),
                  textAlign = TextAlign.Center
                )
              }
              Tab(
                selected = false,
                onClick = {},
                enabled = true,
                modifier = Modifier.fillMaxHeight()
              ) {
                Text("Tab 3", Modifier.padding(3.dp), textAlign = TextAlign.Center)
              }
            }
          }
        }
      }
      File("${System.getProperty("user.home")}/Desktop/screenshot.png").writeBytes(
        window.surface.makeImageSnapshot().encodeToData()!!.bytes
      )
      window.dispose()
      println("Done!")
      save = false
    }
  }
}

fun main(args: Array<String>) = application {
  println("Args " + args.getOrElse(0) { "1.0" })
  Window(onCloseRequest = ::exitApplication) {
    FrameRate()

    Column(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
      val game = remember { Game() }
      var effect by remember { mutableStateOf(true) }
      var delay by remember { mutableStateOf(10) }

      ScreenShot()
      Text("Now using ${type(effect)}", textAlign = TextAlign.Center)
      Spacer(Modifier.height(20.dp))
      Button(onClick = { effect = !effect }) {
        Text("Toggle to ${type(!effect)}", textAlign = TextAlign.Center)
      }

      Spacer(Modifier.height(20.dp))
      TextField(
        value = delay.toString(),
        onValueChange = { newValue -> delay = newValue.toIntOrNull() ?: 10 },
        leadingIcon = { Icon(imageVector = Icons.Default.ArrowBack, "Arrow") },
        shape = RoundedCornerShape(3.dp),
      )

      if (effect) {
        LaunchedEffect(Unit) {
          println("---> Launching effect")
          while (effect) {
            withFrameNanos { game.update(it) }
          }
          println("<--- Exiting effect!")
        }
      } else {
        // Memoize the normal coroutines.
        remember {
          GlobalScope.launch(Dispatchers.Main) {
            println("===> Launching Timer")
            while (!effect) {
              game.update(System.nanoTime())
              delay(delay.toLong())
            }
            println("<=== Exiting Timer!")
          }
        }
      }

      // Text(game.pos.toString())
      Box(
        modifier =
        Modifier.offset(x = game.pos.dp, y = game.pos.dp)
          .clip(CircleShape)
          .size(30.dp)
          .background(Color.Red)
      )

      Row {
        val gif1 by GifRenderer(
          "https://user-images.githubusercontent.com/356994/100579048-4e006a80-3298-11eb-8ea0-a7205221f389.gif",
          true
        )
        Image(gif1, "Gif1", modifier = Modifier.size(125.dp))

        val gif2 by GifRenderer(
          "https://raw.githubusercontent.com/JetBrains/skija/ccf303ebcf926e5ef000fc42d1a6b5b7f1e0b2b5/examples/scenes/images/codecs/animated.gif"
        )
        Image(gif2, "Gif2", modifier = Modifier.size(125.dp))
      }
    }
  }
}

fun type(effect: Boolean) =
  when (effect) {
    true -> "Effect"
    else -> "Timer"
  }

class Game(
  private val velocityPixelsPerSecond: Float = 150f,
  private var previousTimeNanos: Long = Long.MAX_VALUE,
) {

  var pos by mutableStateOf(0f)
    private set

  fun update(nanos: Long) {
    val dt = (nanos - previousTimeNanos).coerceAtLeast(0)
    previousTimeNanos = nanos
    pos += (dt / 1E9 * velocityPixelsPerSecond).toFloat()
    if (pos > 500) pos = 0f
  }
}
