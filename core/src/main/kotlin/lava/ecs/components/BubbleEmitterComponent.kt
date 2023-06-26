package lava.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import twodee.ecs.ashley.components.BodyPart

class BubbleEmitterComponent: Component, Pool.Poolable {
    var emittingBodyPart: BodyPart = BodyPart.Head
    override fun reset() {
        emittingBodyPart = BodyPart.Head
    }

    companion object {
        val mapper = mapperFor<BubbleEmitterComponent>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): BubbleEmitterComponent {
            return mapper.get(entity)
        }
    }
}
