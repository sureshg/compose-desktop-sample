package dev.suresh.jfr

import jdk.jfr.*

@Label("Render Frame")
@Name("dev.suresh.RenderFrame")
@Description("Frame rate counter")
class RenderFrame(
    @Label("Frame Id")
    private var frameId: Long,
) : Event() {

    val count get() = frameId

    /**
     * Generate an updated event. Not thread safe.
     */
    fun inc() {
        begin()
        frameId++
        commit()
    }
}
