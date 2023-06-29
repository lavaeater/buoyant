package lava.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.Shape
import ktx.ashley.allOf
import ktx.math.plus
import ktx.math.vec2
import lava.SfxPlayer
import lava.core.Sfx
import lava.ecs.components.Buoyancy
import lava.ecs.components.DiveControl
import twodee.ecs.ashley.components.Box2d

class DiveControlSystem(private val sfxPlayer: SfxPlayer) : IteratingSystem(allOf(DiveControl::class, Box2d::class).get()) {


    override fun processEntity(entity: Entity, deltaTime: Float) {
        val diveControl = DiveControl.get(entity)

        if(diveControl.isRotating) {
            diveControl.diveVector.rotateDeg(diveControl.rotationFactor * deltaTime * diveControl.rotationSpeed)
        }

        if (diveControl.isDiving) {
            val box2d = Box2d.get(entity)
            val body = box2d.body

            val diveForce = diveControl.diveVector.cpy().scl(diveControl.divingFactor).scl(
                diveControl.diveForce * MathUtils.lerp(
                    0.15f,
                    1.5f,
                    MathUtils.norm(0f, diveControl.strokeTimerDefault, diveControl.strokeTimer)
                )
            )
            body.applyForce(diveForce, body.getWorldPoint(diveControl.diveForceAnchor), true)
            diveControl.strokeTimer -= deltaTime
            if (diveControl.strokeTimer < 0f) diveControl.strokeTimer = diveControl.strokeTimerDefault
        } else {
            diveControl.strokeTimer = diveControl.strokeTimerDefault
        }
        fixBreathing(entity, diveControl, deltaTime)
    }

    private var diaphragmCoolDown = -1f

    private var wasUnderWater = false

    private fun fixBreathing(entity: Entity, diveControl: DiveControl, deltaTime: Float) {
        if (diveControl.isUnderWater) {
            wasUnderWater = true
            if (diveControl.hasAny()) {
                diveControl.airSupply -= deltaTime * 2.5f
            } else {
                diveControl.airSupply -= deltaTime * 1f
            }
            if(diveControl.airSupply < 30f) {
                if(diaphragmCoolDown < 0f) {
                    diaphragmCoolDown = 9f
                    sfxPlayer.playSound(Sfx.Diaphragm)
                } else {
                    diaphragmCoolDown -= deltaTime
                }
            }
        } else {
            if(wasUnderWater) {
                wasUnderWater = false
                sfxPlayer.playSound(Sfx.Breath)
            }
            diveControl.airSupply = 100f
        }
    }
}

fun Shape.getPosition(): Vector2 {
    if (this is CircleShape) {
        return position.cpy()
    } else if (this is PolygonShape) {
        val vertices = mutableListOf<Vector2>()
        for (i in 0 until this.vertexCount) {
            val vertex = vec2()
            getVertex(i, vertex)
            vertices.add(vertex)
        }
        val polygon = Polygon(vertices.flatMap { listOf(it.x, it.y) }.toFloatArray())
        return polygon.getCentroid(vec2())
    }
    return vec2()
}
