package lava.ecs.systems

import com.badlogic.ashley.core.EntitySystem
import lava.music.MusicPlayer

class MusicSystem(private val musicPlayer: MusicPlayer): EntitySystem(){
    override fun update(deltaTime: Float) {
        musicPlayer.update()
    }
}
