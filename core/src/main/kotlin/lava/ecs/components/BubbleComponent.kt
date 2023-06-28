package lava.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class BubbleComponent: Component, Pool.Poolable {
    var radius = 2f
    var lifeSpanRange = 5..10
    var lifeSpan = lifeSpanRange.random().toFloat()

    override fun reset() {
radius = 2f
        lifeSpan = lifeSpanRange.random().toFloat()
    }

    companion object {
        val mapper = mapperFor<BubbleComponent>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): BubbleComponent {
            return mapper.get(entity)
        }
    }
}
