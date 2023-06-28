package lava.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.math.times
import ktx.math.vec2
import lava.ecs.components.BubbleComponent
import twodee.ecs.ashley.components.Box2d
import kotlin.math.pow

class BubbleBuoyancySystem:IteratingSystem(allOf(BubbleComponent::class, Box2d::class).get()) {
    val directionVector = vec2(0f,1f)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val angle = directionVector.angleDeg() - 90f
        if(angle > 0f) {
            directionVector.rotateDeg((-25..0).random().toFloat())
        } else {
            directionVector.rotateDeg((0..25).random().toFloat())
        }
        val box2d = Box2d.get(entity)
        box2d.body.applyForceToCenter(directionVector, true)

        val velDir = box2d.body.linearVelocity.cpy()
        val velMag = velDir.len()
        velDir.nor()

        //apply simple linear drag
        val dragMag = box2d.body.mass * velMag.pow(2)
        val dragForce = velDir.scl(-1f).scl(dragMag)
        box2d.body.applyForceToCenter(dragForce, true)
    }
}
