package lava.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import lava.ecs.components.DiveControl
import lava.music.MusicPlayer

class MusicSystem(private val musicPlayer: MusicPlayer): IteratingSystem(allOf(DiveControl::class).get()){

    override fun setProcessing(processing: Boolean) {
        super.setProcessing(processing)
        if(processing)
            musicPlayer.play()
        else
            musicPlayer.stop()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val airSupply = DiveControl.get(entity).airSupply
        musicPlayer.intensity = (100f - airSupply) / 100f
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        musicPlayer.update()
    }
}
