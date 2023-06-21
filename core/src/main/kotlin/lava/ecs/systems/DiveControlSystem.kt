package lava.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.math.plus
import ktx.math.vec2
import lava.ecs.components.Buoyancy
import lava.ecs.components.DiveControl
import twodee.ecs.ashley.components.Box2d

class DiveControlSystem: IteratingSystem(allOf(DiveControl::class, Box2d::class, Buoyancy::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val diveControl = DiveControl.get(entity)
        if(diveControl.hasAny()) {
            val box2d = Box2d.get(entity)
            val buoyancy = Buoyancy.get(entity)
            val body = box2d.body
            val diveForce = diveControl.getVector().cpy().scl(5f)
            body.applyForce(diveForce, buoyancy.checkForWaterPoint + body.position + vec2(-0.2f, 0f), true)
        }
    }
}
