@file:JvmName("TeaVMLauncher")

package lava.core.teavm

import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration
import com.github.xpenatan.gdx.backends.teavm.TeaApplication
import lava.core.BuoyantGame

/** Launches the TeaVM/HTML application. */
fun main() {
    val config = TeaApplicationConfiguration("canvas").apply {
        width = 0
        height = 0
    }
    TeaApplication(BuoyantGame(), config)
}
