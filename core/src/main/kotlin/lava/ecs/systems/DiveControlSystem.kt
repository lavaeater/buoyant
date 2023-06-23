package lava.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.Shape
import ktx.ashley.allOf
import ktx.math.plus
import ktx.math.vec2
import lava.ecs.components.Buoyancy
import lava.ecs.components.DiveControl
import twodee.ecs.ashley.components.Box2d

class DiveControlSystem: IteratingSystem(allOf(DiveControl::class, Box2d::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val diveControl = DiveControl.get(entity)
        if(diveControl.hasAny()) {
            val box2d = Box2d.get(entity)
//            val head = box2d.bodies["head"]!!
            val body = box2d.body
            val head = body.fixtureList.first { it.userData == "head" }
            val diveForce = diveControl.getVector().cpy().scl(50f)
            body.applyForce(diveForce,body.getWorldPoint(head.shape.getPosition()), true)
        }
    }
}

fun Shape.getPosition(): Vector2 {
    if(this is CircleShape) {
        return position.cpy()
    }
    return vec2()
}
