package lava

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import ktx.assets.toInternalFile
import lava.core.Assets
import lava.core.Sfx

class SfxPlayer(val assets: Assets) {
    private val sfxLibrary = mutableMapOf<Sfx, Sound>()
    private val playingNow = mutableMapOf<Sfx, Float>()

    fun playSound(sfx: Sfx) {
        if(playingNow.containsKey(sfx))
            return

        if(!sfxLibrary.containsKey(sfx)) {
            sfxLibrary[sfx] = Gdx.audio.newSound(sfx.path.toInternalFile())
        }

        playingNow[sfx] = sfx.duration
        sfxLibrary[sfx]!!.play(sfx.volume)
    }

    fun update(delta: Float) {
        for(fx in playingNow.keys) {
            playingNow[fx] = playingNow[fx]!! - delta
        }

        val toRemove = playingNow.filter { it.value <= 0f }.keys
        toRemove.forEach { playingNow.remove(it) }
    }
}
