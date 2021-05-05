package dev.suresh.vsync

import java.awt.*
import java.util.concurrent.*
import java.util.concurrent.locks.*
import javax.swing.*

fun main() {
    val refreshRate =
        GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices.map {
            it.displayMode.refreshRate
        }
    println("Display refresh rates: $refreshRate")

    Thread.sleep(2000)
    test("SwingTimer") { action ->
        Timer(2) { action() }.apply {
            isRepeats = false
            start()
        }
    }

    val executor = Executors.newScheduledThreadPool(8)
    test("JavaExecutor") { action -> executor.schedule(action, 2, TimeUnit.MILLISECONDS) }

    val blocker = Any()
    test("parkNanos") { action ->
        LockSupport.parkNanos(blocker, 2_000_000)
        action()
    }
}

private fun test(name: String, block: (() -> Unit) -> Unit) {
    var i = 0
    fun schedule() {
        val t1 = System.nanoTime()
        block {
            val t2 = System.nanoTime()
            val elapsed = (t2 - t1) / 1E6
            println("$name $elapsed")
            Thread.sleep(100)
            if (i++ < 8) {
                schedule()
            }
        }
    }
    schedule()
    Thread.sleep(2000)
}
