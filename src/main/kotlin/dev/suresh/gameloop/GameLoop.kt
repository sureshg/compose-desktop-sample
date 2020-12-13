package dev.suresh.gameloop

import androidx.compose.desktop.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.dispatch.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*

fun main() = Window {
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
            leadingIcon = { Icon(imageVector = Icons.Default.ArrowBack) },
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

fun type(effect: Boolean) = when (effect) {
    true -> "Effect"
    else -> "Timer"
}

class Game(
    private val velocityPixelsPerSecond: Float = 150f,
    private var previousTimeNanos: Long = Long.MAX_VALUE
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
