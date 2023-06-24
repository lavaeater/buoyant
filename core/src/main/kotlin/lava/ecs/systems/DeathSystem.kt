package lava.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import lava.core.BuoyantGame
import lava.ecs.components.DiveControl

class DeathSystem(private val game: BuoyantGame):IteratingSystem(allOf(DiveControl::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val diveControl = DiveControl.mapper.get(entity)
        if (diveControl.airSupply < 0.0f) {
            game.goToGameOver()
        }
    }
}
