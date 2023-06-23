package lava.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.CircleShape
import ktx.ashley.allOf
import lava.ecs.components.DiveControl
import lava.ecs.components.PolygonComponent
import lava.ecs.components.WaterComponent
import twodee.ecs.ashley.components.Box2d

class HeadUnderWaterSystem:IteratingSystem(allOf(Box2d::class, DiveControl::class).get()) {

    private val waterFamily = allOf(Box2d::class, WaterComponent::class).get()
    private val waterEntity get() = engine.getEntitiesFor(waterFamily).first()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        /*
        Simply check if the head fixture is inside the water polygon
         */
        val box2d = Box2d.get(entity)
        val head = box2d.body.fixtureList.first { it.userData == "head" }.shape as CircleShape

        val waterPolygon = PolygonComponent.get(waterEntity).polygon
        val headPosition = box2d.body.getWorldPoint(head.position)
        DiveControl.get(entity).isUnderWater = waterPolygon.contains(headPosition.x, headPosition.y)
    }
}
