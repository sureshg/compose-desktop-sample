package dev.suresh.jfr

import jdk.jfr.*

@Label("Frame Rate")
@Name("dev.suresh.RenderFrame")
@Description("Frame rate (fps)")
class FrameRate(
    @Label("FPS") private var fRate: Int,
) : Event() {

  /** Generate an FPS event. Not thread safe. */
  var fps: Int
    get() = fRate
    set(fps) {
      begin()
      this.fRate = fps
      commit()
    }
}
