package lava.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import lava.ecs.components.DiveControl
import lava.ecs.components.FlashlightComponent
import twodee.ecs.ashley.components.TransformComponent

class PlayerFlashlightSystem : IteratingSystem(
    allOf(
        FlashlightComponent::class,
        TransformComponent::class,
        DiveControl::class
    ).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val flashlight = FlashlightComponent.get(entity).flashLight
        val playerPosition = TransformComponent.get(entity).position
        val aimVector = DiveControl.get(entity).diveVector.cpy()
        if(!aimVector.x.isNaN()) {
            flashlight.direction = aimVector.angleDeg()
            aimVector.scl(2.5f)
            flashlight.setPosition(playerPosition.x + aimVector.x, playerPosition.y + aimVector.y)
        }
    }
}
