package lava

import lava.core.Assets
import lava.core.Sfx

class SfxPlayer(val assets: Assets) {
    fun playSound(sfx: Sfx, volume: Float) {
        if(assets.sfx.containsKey(sfx)) {
            assets.sfx[sfx]!!.play(volume)
        }
    }
}
