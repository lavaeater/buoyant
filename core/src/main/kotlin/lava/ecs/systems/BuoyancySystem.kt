package lava.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.math.minus
import ktx.math.plus
import lava.ecs.components.Buoyancy
import lava.ecs.components.PolygonComponent
import lava.ecs.components.WaterComponent
import twodee.ecs.ashley.components.Box2d

class BuoyancySystem : IteratingSystem(allOf(Buoyancy::class, Box2d::class).get()) {
    private val waterFamily = allOf(WaterComponent::class).get()
    private val waterEntities get() = engine.getEntitiesFor(waterFamily)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val buoyancy = Buoyancy.get(entity)
        val box2d = Box2d.get(entity)
        val body = box2d.body
        val buoyancyForce = buoyancy.buoyancyForce.cpy().scl(body.mass)
        val checkForWaterPoint = buoyancy.checkForWaterPoint + body.position

        if(waterEntities.any()) {
            val waterPolygon = PolygonComponent.get(waterEntities.first()).polygon
            if(!waterPolygon.contains(checkForWaterPoint)) {
                buoyancyForce.set(0f, 9.5f).scl(body.mass)
            }
        }

        body.applyForce(buoyancyForce, body.getWorldPoint(buoyancy.buoyancyOffset), true)
    }
}
