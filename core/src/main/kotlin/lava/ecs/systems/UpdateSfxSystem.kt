package lava.ecs.systems

import com.badlogic.ashley.core.EntitySystem
import lava.SfxPlayer
import lava.core.Sfx

class UpdateSfxSystem(private val sfxPlayer: SfxPlayer):EntitySystem() {
    var fxCoolDown = 5f

    override fun update(deltaTime: Float) {
        sfxPlayer.update(deltaTime)
        fxCoolDown -= deltaTime
        if(fxCoolDown < 0f) {
            fxCoolDown = (5..10).random().toFloat()
            sfxPlayer.playSound(if((1..2).random() == 1) Sfx.Ambient else Sfx.Creak)
        }
    }
}
