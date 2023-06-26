package lava.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import lava.core.EntityFactory
import lava.ecs.components.BubbleEmitterComponent
import lava.ecs.components.DiveControl
import twodee.ecs.ashley.components.BodyPart
import twodee.ecs.ashley.components.Box2d
import twodee.injection.InjectionContext.Companion.inject

class BubbleSystem:IteratingSystem(allOf(DiveControl::class, BubbleEmitterComponent::class, Box2d::class).get()) {

    private val coolDownRange = 1..10
    private var coolDown = coolDownRange.random() / 10f
    private val entityFactory: EntityFactory by lazy { inject() }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        if(DiveControl.get(entity).isUnderWater) {
            coolDown -= deltaTime
            if (coolDown < 0f) {
                coolDown = coolDownRange.random() / 10f
                val bodyPart = BubbleEmitterComponent.get(entity).emittingBodyPart
                val box2d = Box2d.get(entity)
                val emittingFixture =
                    box2d.bodies.values.flatMap { it.fixtureList }.associateBy { it.userData as BodyPart }[bodyPart]!!
                val emittingBody = emittingFixture.body
                val emittingPosition = emittingBody.getWorldPoint(emittingFixture.shape.getPosition())
                entityFactory.emitBubble(emittingPosition, 0.1f)
            }
        }
    }
}
