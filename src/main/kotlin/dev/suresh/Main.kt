package dev.suresh

import androidx.compose.animation.*
import androidx.compose.desktop.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.ripple.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import dev.suresh.theme.*
import java.awt.*
import java.awt.dnd.*
import kotlinx.coroutines.*

fun main() = Window(title = "Compose Desktop", centered = true) { App() }

@OptIn(InternalCoroutinesApi::class)
@Composable
fun Test(name: String = "Kotlin") {
  var count by mutableStateOf(0)

  println("Launching effect...")

  DesktopMaterialTheme {
    Row {
      Box(modifier = Modifier.fillMaxSize(0.5f)) {
        Column {
          var index by remember { mutableStateOf(1) }
          TabRow(selectedTabIndex = index) {
            (1..5).forEach {
              Tab(selected = true, onClick = {}) {
                Button(
                    modifier = Modifier.padding(2.dp),
                    shape = RoundedCornerShape(2.dp),
                    onClick = {
                      println("Clicked the tab-$it")
                      index = it
                    },
                ) {
                  Text(
                      text = "Tab-$it",
                      //  fontFamily = fontFamily(font("rboto","")),
                      )
                }
              }
            }
          }
        }
      }
      Box(modifier = Modifier.fillMaxSize(1f)) {
        println("Composing the column...")
        Text(
            "Hello $name. Clicked $count times",
            modifier =
                Modifier.padding(10.dp).align(Alignment.TopCenter).clickable {
                  println("Clicked the Text!!")
                  count++
                },
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h4)

        var gCount by remember { mutableStateOf(0) }

        val label by onEachFrame()

        Button(
            modifier =
                Modifier.padding(2.dp).align(Alignment.Center).animateContentSize { _, _ -> },
            shape = RoundedCornerShape(2.dp),
            onClick = {
              gCount++
              println("global count....$gCount")
            },
        ) {
          println("Composing global....$label")
          Text(text = "Click $label")
        }

        LaunchedEffect(count) {
          this.coroutineContext.job.invokeOnCompletion(
              onCancelling = true, invokeImmediately = true) {
            println("\n >>>>>> Cancelling ${it?.message}")
          }
          println("Waiting 3 sec")
          delay(3000)
          count += 10
        }
      }
    }

    addDropTarget()
  }
}

@Composable
private fun addDropTarget() {
  val target =
      object : DropTarget() {

        @Synchronized
        override fun drop(dtde: DropTargetDropEvent?) {
          println("Dropped here ${dtde?.transferable}")
        }
      }
  LocalAppWindow.current.window.dropTarget = target
}

@Composable
fun onEachFrame(): MutableState<Long> {

  println("!!!! Composing onEachFrame")
  val frame = remember { mutableStateOf(0L) }
  LaunchedEffect(Unit) {
    println("!!!! Starting onEachFrame${frame.value}")
    while (isActive) {
      //            withFrameMillis {
      //                frame.value += 1
      //                println("!!!! Updating onEachFrame value")
      //            }
      delay(1000)
      frame.value += 1L
    }
  }
  return frame
}

@Composable
private fun App() {
  var text by remember { mutableStateOf("Hello, Kotlin!") }
  val state = rememberScaffoldState()
  val showDialog = remember { mutableStateOf(false) }

  val pieces =
      listOf(
          Piece(
              "rook",
              "chess/b_rook_2x_ns.png",
              "dbdfmbdsfmnbd\ndfdsfsdf\nsfdsfd\ndfdsfdsfd\ndfsdfdfdfdsfdsfdsfsdfdsfdsfsdfdfdfdsfdfdsfdf"),
          Piece(
              "pawn",
              "chess/w_pawn_2x_ns.png",
              "dbdfmbdsfmnbd\ndfdsfsdf\nsfdsfd\ndfdsfdsfd\ndfsdfdffdsfdsfdsfdfdsfdsfdfdsfdfdsfds"),
          Piece(
              "knight",
              "chess/b_knight_2x_ns.png",
              "dbdfmbdsfmnbd\ndfdsfsdf\nsfdsfd\ndfdsfdsfd\ndfsdfdfdfsfdsfdsfdfdfdfdsfdsfsdfdfdf"))
  MaterialTheme {
    Scaffold(
        topBar = { TopBar("Jetbrains Compose Demo", state) },
        bottomBar = { BottomBar(state) },
        floatingActionButton = { showFab(showDialog) },
        drawerElevation = 10.dp,
        drawerContent = { NavBar(state) },
        drawerShape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
        scaffoldState = state) {
      Column(modifier = Modifier.fillMaxSize()) {
        Button(
            modifier = Modifier.padding(2.dp),
            shape = RoundedCornerShape(2.dp),
            onClick = { text = "Yay..clicked!" },
        ) { Text(text = text) }

        LazyColumn {
          pieces.forEach {
            println("Adding chess card")
            item { ChessCard(it) }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollBar() {
  Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
    val stateHorizontal = rememberLazyListState()

    val cols = (0..30).toList()

    Column {
      LazyColumn(state = stateHorizontal) {
        items(cols) { i ->
          Box(
              modifier =
                  Modifier.border(width = 1.dp, color = MaterialTheme.colors.onBackground)
                      .size(80.dp, 20.dp)) { Text("data_$i") }
        }
      }

      LazyColumn(state = stateHorizontal) {
        items(cols) { i ->
          Box(
              modifier =
                  Modifier.border(width = 1.dp, color = MaterialTheme.colors.onBackground)
                      .size(80.dp, 20.dp)) { Text("data_$i") }
        }
      }
    }

    HorizontalScrollbar(
        modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth(),
        adapter =
            rememberScrollbarAdapter(
                scrollState = stateHorizontal, itemCount = cols.size, averageItemSize = 80.dp))
  }
}

@Immutable
data class Piece(
    val name: String,
    val image: String,
    val desc: String,
)

@Composable
fun ChessCard(piece: Piece) {

  val img =
      remember(piece.image) {
        println("Loading image for $piece")
        imageFromResource(piece.image)
      }

  Card(
      modifier = Modifier.padding(4.dp).size(400.dp, 100.dp),
      backgroundColor = Color.LightGray,
      elevation = 3.dp) {
    Column {
      Image(
          bitmap = img,
          contentDescription = "Image",
          modifier = Modifier.padding(3.dp).size(30.dp).clip(RoundedCornerShape(5.dp)))
      Spacer(Modifier.size(10.dp))
      Text(text = piece.name, style = typography.h6)
      Text(
          text = piece.desc,
          style = typography.body2,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis)
    }
  }
}

@Composable
fun showFab(showDialog: MutableState<Boolean>) {
  println("Recomposing..... >>>>")
  val window = LocalAppWindow.current

  FloatingActionButton(onClick = { showDialog.value = true }) { Icon(Icons.Default.Home, "Home") }

  if (showDialog.value) {

    Popup(alignment = Alignment.Center) { Text("Hello") }

    //        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    //        val chooser = JFileChooser()
    //        val filter = FileNameExtensionFilter(
    //            "JPG & GIF Images", "jpg", "gif"
    //        )
    //        chooser.fileFilter = filter
    //        val returnVal = chooser.showOpenDialog(null)
    //        if (returnVal == JFileChooser.APPROVE_OPTION) {
    //            println(
    //                "You chose to open this file: " +
    //                        chooser.selectedFile.name
    //            )
    //        }

    val dialog =
        FileDialog(window.window, "Select File to Open").apply {
          mode = FileDialog.LOAD
          isVisible = true
        }
    val file: String? = dialog.file
    println("$file chosen.")

    //
    //        Dialog(
    //            title = "Dialog Title",
    //            size = IntSize(100, 100),
    //            centered = true,
    //            icon = null,
    //            menuBar = MenuBar(Menu("Test")),
    //            undecorated = false,
    //            events = WindowEvents(
    //                onClose = {
    //                    println("Closing the dialog")
    //                    showDialog.value = false
    //                },
    //            ),
    //            onDismissRequest = {
    //                println("Dismissing the dialog")
    //            },
    //        ) {
    //
    //            // AppManager.windows.last() as? AppWindow
    //            val c = AppWindowAmbient.current
    //            c?.keyboard?.setShortcut(Key.Escape) {
    //                println("Got escape...closing..")
    //                c.close()
    //            }
    //            Text(text = "OK")
    //        }
    showDialog.value = false
  }

  //    println("Show Dialog....")
  //    AlertDialog(
  //        onDismissRequest = {
  //            showDialog.value = false
  //        },
  //        confirmButton = {
  //            Button(onClick = {
  //                // reset the value to false, which will make the dialog go away
  //                showDialog.value = false
  //            }) {
  //                Text(text = "OK")
  //            }
  //        },
  //        dismissButton = {
  //            Button(onClick = {
  //                // reset the value to false, which will make the dialog go away
  //                showDialog.value = false
  //            }) {
  //                Text(text = "Cancel")
  //            }
  //        },
  //        title = {
  //            Text(text = "Alert Dialog")
  //        },
  //        text = {
  //            Text(text = "Compose Desktop")
  //        },
  //        shape = RoundedCornerShape(5.dp),
  //        backgroundColor = MaterialTheme.colors.onPrimary,
  //    )
}

@Composable
fun TopBar(name: String, scaffoldState: ScaffoldState) {

  val cs = rememberCoroutineScope()

  TopAppBar(
      title = { Text(name) },
      elevation = 10.dp,
      navigationIcon = {
        IconButton(
            onClick = { cs.launch { scaffoldState.drawerState.open() } },
        ) { Icon(imageVector = Icons.Rounded.ArrowBack, "Arrow") }
      },
  )

  if (scaffoldState.drawerState.isOpen) {
    println(">>>>>> Scheduling the the Timer")
    LaunchedEffect(cs) {
      delay(3000)
      scaffoldState.drawerState.close()
      withContext(Dispatchers.IO) {}
    }
  }
}

@Composable
fun BottomBar(state: ScaffoldState) {
  Column {
    BottomAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        cutoutShape = RoundedCornerShape(10.dp),
        elevation = 10.dp,
    ) { Text("This is a Bottom bar") }

    if (state.drawerState.isClosed) {
      Snackbar(
          action = {
            ClickableText(buildAnnotatedString { append("Click") }) { println("Closing...") }
          }) { Text(text = "This is a snackbar!") }
    }
  }
}

@Composable
fun NavBar(scaffoldState: ScaffoldState) {
  println("#### Recompose NavBar #######")

  Column {
    Image(
        bitmap = imageFromResource("humming.jpg"),
        contentDescription = "Hummingbird",
        modifier = Modifier.padding(5.dp).size(100.dp, 100.dp),
    )
    Divider()

    val items = List(100) { "ItemIndexd-$it" }

    val cs = rememberCoroutineScope()

    LazyColumn(
        contentPadding = PaddingValues(2.dp),
    ) {
      itemsIndexed(
          items,
          itemContent = { i, t ->
            Text(
                text = t,
                fontFamily = FontFamily.Default,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier =
                    Modifier.padding(20.dp).fillMaxWidth().clickable(
                            indication = rememberRipple(true, color = Color.Green),
                            interactionSource = remember { MutableInteractionSource() }) {
                      cs.launch { scaffoldState.drawerState.close() }
                    },
            )
          },
      )
    }
  }
}
