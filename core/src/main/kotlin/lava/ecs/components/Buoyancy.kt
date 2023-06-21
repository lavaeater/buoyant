package lava.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import ktx.math.vec2

class Buoyancy: Component, Pool.Poolable {
    val checkForWaterPoint = vec2()
    val buoyancyForce = vec2(0f, 12f)
    val buoyancyOffset = vec2()
    lateinit var fixture:Fixture
    override fun reset() {
        buoyancyOffset.setZero()
        buoyancyForce.setZero()
        checkForWaterPoint.setZero()
    }

    companion object {
        val mapper = mapperFor<Buoyancy>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): Buoyancy {
            return mapper.get(entity)
        }
    }
}
