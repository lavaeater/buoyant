package lava.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import lava.SfxPlayer
import lava.core.BuoyantGame
import lava.core.Sfx
import lava.ecs.components.DiveControl
import lava.music.MusicPlayer

class DeathSystem(
    private val game: BuoyantGame,
    private val sfxPlayer: SfxPlayer,
    private val musicPlayer: MusicPlayer
) : IteratingSystem(allOf(DiveControl::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val diveControl = DiveControl.mapper.get(entity)
        if (diveControl.airSupply < 0.0f) {
            musicPlayer.stop()
            sfxPlayer.playSound(Sfx.Drown, 1f)
            diveControl.dead = true
            game.goToGameOver()
        }
    }
}
