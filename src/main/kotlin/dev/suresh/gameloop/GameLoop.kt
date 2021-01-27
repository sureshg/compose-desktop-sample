package dev.suresh.gameloop

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.dispatch.withFrameNanos
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.suresh.jfr.RenderFrame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val renderFrame = RenderFrame(0)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FrameRate() {
    var frameRate by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            var prevCount = renderFrame.count
            while (isActive) {
                val count = renderFrame.count
                delay(1000)
                frameRate = count - prevCount
                prevCount = count
                // println(Thread.currentThread().name)
            }
        }
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            withFrameNanos {
                renderFrame.inc()
            }
        }
    }

    var selected by remember { mutableStateOf(false) }
    val corner by animateDpAsState(if (selected) 10.dp else 0.dp)
    val color by animateColorAsState(if (selected) Color.Red else Color.Green)

    Column(
        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopCenter)
    ) {
        Text(
            "Frame rate: $frameRate fps",
            modifier = Modifier.border(
                1.dp, color,
                RoundedCornerShape(corner),
            ).background(Color.Yellow).padding(12.dp).pointerMoveFilter(
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

    Box {
        val state = rememberLazyListState()
        LazyColumn(
            modifier = Modifier.padding(10.dp),
            state = state,
            reverseLayout = false,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            items((1..100).toList()) { x ->
                Text(
                    x.toString(),
                    modifier = Modifier.graphicsLayer(alpha = 0.5f).height(20.dp)
                )
            }
        }

        Spacer(Modifier.padding(horizontal = 30.dp))
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(state, 100, 20.dp),
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

fun main(args: Array<String>) {
    println("Args " + args[0])
    Window {
        FrameRate()
        Column(
            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
        ) {

            val game = remember { Game() }
            var effect by remember { mutableStateOf(true) }
            var delay by remember { mutableStateOf(10) }

            Text("Now using ${type(effect)}", textAlign = TextAlign.Center)
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    effect = !effect
                }
            ) {
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
                        withFrameNanos {
                            game.update(it)
                        }
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
                modifier = Modifier
                    .offset(x = game.pos.dp, y = game.pos.dp)
                    .clip(CircleShape)
                    .size(30.dp)
                    .background(Color.Red)
            )
        }
    }
}

fun type(effect: Boolean) = when (effect) {
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
