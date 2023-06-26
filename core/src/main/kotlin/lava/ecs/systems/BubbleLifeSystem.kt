package lava.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import lava.ecs.components.BubbleComponent
import twodee.ecs.ashley.components.Remove
import twodee.physics.addComponent

class BubbleLifeSystem: IteratingSystem(allOf(BubbleComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val bubble = BubbleComponent.get(entity)
        bubble.lifeSpan-= deltaTime
        if(bubble.lifeSpan < 0f) {
            entity.addComponent<Remove>()
        }
    }
}
