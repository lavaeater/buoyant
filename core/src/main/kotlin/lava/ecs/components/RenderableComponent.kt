package lava.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor


class RenderableComponent: Component, Pool.Poolable {
    var zIndex = 0

    override fun reset() {
        zIndex = 0
    }

    companion object {
        val mapper = mapperFor<RenderableComponent>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): RenderableComponent {
            return mapper.get(entity)
        }
    }
}
