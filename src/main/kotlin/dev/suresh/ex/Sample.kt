package dev.suresh.ex

import androidx.compose.animation.core.*
import androidx.compose.desktop.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*

data class Product(
  val name: String,
  val count: Int,
)

fun main() = Window {

  //    val products = listOf(
  //        Product("Milk", 1),
  //        Product("Egg", 12)
  //    )
  //    App(products)

  val (check, setCheck) = remember { mutableStateOf(true) }

  var count by remember { mutableStateOf(0) }

  val s by derivedStateOf { count }

  Column {
    Checkbox(
      checked = check,
      onCheckedChange = setCheck,
    )

    Button(onClick = { count++ }) { Text(text = count.toString()) }
    if (check) {
      Test("Kotlin $count")
    } else {
      Test("Java")
    }
  }
}

@Composable
fun Test(name: String) {
  println(">>> Invoking the $name function!")
  Text("Hello $name")

  DisposableEffect(Unit) {
    println("$name composable activated")
    onDispose { println("$name composable deleted!") }
  }

  DisposableEffect(name) { onDispose { println("$name composable committed!") } }
}

@Composable
fun App(products: List<Product>) {

  var filter by remember { mutableStateOf("") }

  Column {
    TextField(
      value = filter,
      onValueChange = { filter = it },
      label = { Text("Filter Text") },
      placeholder = null,
      leadingIcon = { Icon(imageVector = Icons.Default.Menu, "Menu") },
      shape = RoundedCornerShape(3.dp)
    )

    products.filter { it.name.contains(filter, true) }.forEach { Text("${it.name} - ${it.count}") }

    val (shape, setShape) = remember { mutableStateOf<Shape>(CircleShape) }

    val (alpha, setAlpha) = remember { mutableStateOf(1.0f) }

    val img = remember { imageFromResource("humming.jpg") }

    Image(
      bitmap = img,
      contentDescription = "Image",
      contentScale = ContentScale.Crop,
      alpha = alpha,
      modifier =
      Modifier.size(256.dp)
        .padding(5.dp)
        .clip(shape)
        .border(4.dp, MaterialTheme.colors.primary, shape)
        .border(8.dp, MaterialTheme.colors.secondary, shape)
        .border(12.dp, MaterialTheme.colors.background, shape)
        .clickable(
          indication = rememberRipple(),
          interactionSource = remember { MutableInteractionSource() }
        ) {
          setShape(
            when (shape) {
              CircleShape -> CutCornerShape(20.dp)
              else -> CircleShape
            }
          )
        }
    )

    Slider(value = alpha, onValueChange = setAlpha, valueRange = 0.0f..1.0f, steps = 0)

    val (elv, setElv) = remember { mutableStateOf(0f) }

    Surface(
      modifier = Modifier.padding(5.dp).clickable { println("Clicked") },
      shape = MaterialTheme.shapes.medium,
      color = MaterialTheme.colors.primary,
      contentColor = MaterialTheme.colors.surface,
      elevation = elv.dp,
    ) { Text("Chip Example", modifier = Modifier.padding(10.dp)) }

    Text("Adjust the elevation: ${elv.toInt()} dp")
    Slider(
      value = elv,
      onValueChange = setElv,
      valueRange = 1f..100f,
    )

    var clicked by remember { mutableStateOf(false) }

    val size by animateDpAsState(
      targetValue = if (clicked) 100.dp else 50.dp,
      animationSpec =
      tween(durationMillis = 3_00, delayMillis = 0, easing = LinearOutSlowInEasing),
      finishedListener = { clicked = !clicked }
    )

    Surface(
      modifier =
      Modifier.padding(20.dp)
        .size(size)
        .align(alignment = Alignment.CenterHorizontally)
        .clickable { clicked = !clicked },
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colors.primary,
      contentColor = MaterialTheme.colors.secondary,
      border = BorderStroke(0.dp, Color.Red),
      elevation = 20.dp,
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          "Kotlin",
          textAlign = TextAlign.Center,
        )
      }
    }
  }
}

fun ProductLabel() {}
