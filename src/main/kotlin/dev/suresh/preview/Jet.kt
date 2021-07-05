package dev.suresh.preview

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.desktop.*
import androidx.compose.desktop.ui.tooling.preview.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = Window {
  App()
}

/**
 * - https://easings.net/
 * - https://cubic-bezier.com/#0,-1.27,.59,.77
 */
@Composable
@Preview
fun App() {

  val state = rememberScaffoldState()
  // val scope = rememberCoroutineScope()

  Scaffold(scaffoldState = state) {

    var counter by remember { mutableStateOf(0) }

    val asyncCounter by produceState(0) {
      while (true) {
        delay(1000)
        value += 1
      }
    }

//    val ss =  flow  {
//
//    }
//    val kk =ss.collectAsState(1)

    Column {
      Button(
        onClick = {
          counter++
        }
      ) {
        Text("Clicked: $counter")
      }

      AnimatedVisibility(asyncCounter > 0) {
        Text("Counter: $asyncCounter", style = MaterialTheme.typography.h6)
      }

      val infiniteTransition = rememberInfiniteTransition()
      val color by infiniteTransition.animateColor(
        Color.Green,
        Color.Cyan,
        infiniteRepeatable(
          animation = tween(
            durationMillis = 2000, delayMillis = 0, easing = LinearOutSlowInEasing
          ),
          repeatMode = RepeatMode.Reverse
        )
      )

      var sizeState by remember { mutableStateOf(200.dp) }
      val size by animateDpAsState(
        targetValue = sizeState,
        animationSpec = tween(
          durationMillis = 1000,
          delayMillis = 0,
          easing = CubicBezierEasing(
            0.68f,
            -0.6f,
            0.32f,
            1.6f
          ) // Easing {  } or FastOutLinearInEasing
        )
//       animationSpec = spring(Spring.DampingRatioMediumBouncy,Spring.StiffnessMedium)
//        animationSpec = keyframes {
//          delayMillis = 100
//          durationMillis = 3000
//          sizeState at 0 with FastOutLinearInEasing
//          sizeState * 1.2f at 1000 with FastOutSlowInEasing
//          sizeState * 1.5f at durationMillis with LinearOutSlowInEasing
//        }
      )

      Box(
        modifier = Modifier.size(size)
          .background(color),
        contentAlignment = Alignment.Center
      ) {
        Column {
          Button(
            onClick = {
              sizeState += 50.dp
            }
          ) {
            Text("Increase!!")
          }

          Spacer(Modifier)

          Button(
            onClick = {
              sizeState -= 50.dp
            }
          ) {
            Text("Decrease!!")
          }
        }
      }
    }

    if (counter % 5 == 0 && counter > 0) {
      // scope.launch {
      //  state.snackbarHostState.showSnackbar("Hello $counter")
      // }
      LaunchedEffect(state.snackbarHostState) {
        state.snackbarHostState.showSnackbar("Hello $counter")
      }
    }
  }
}

//
// var i = 0
//
// @OptIn(ExperimentalAnimationApi::class)
// @Composable
// fun App() {
//  var a by remember { mutableStateOf(0) }
//  val b by remember { derivedStateOf { a > 5 } }
//
//  Column {
//    Card {
//      var expanded by remember { mutableStateOf(true) }
//
//      Column(modifier = Modifier.clickable { expanded = !expanded }) {
//        Text("OK")
//        AnimatedVisibility(expanded) {
//          Text(
//            text = "Jetpack Compose Demo",
//            style = MaterialTheme.typography.h4
//          )
//        }
//
//
//        Button(
//          onClick = {
//            a++
//            if (a > 10) a = 0
//          }
//        ) {
//          Column {
//            Text("Click : $b")
//            if (a > 5) {
//              Comp1()
//            } else {
//              Comp2()
//            }
//          }
//
//        }
//      }
//    }
//  }
// }
//
// var g = 0
// @Composable
// fun Comp1() {
//
//  SideEffect {
//    println("Text1: ${g++}")
//  }
//
//  Text("Text1")
//  println("Composing 1")
// }
//
// @Composable
// fun Comp2() {
//
//  DisposableEffect(Unit) {
//    println("Composing Dispose 2")
//    onDispose {
//      println("Disposing Text2")
//    }
//  }
//  SideEffect {
//    println("Text2: ${g++}")
//  }
//
//  Text("Text2")
//  println("Composing 2")
//
// }
