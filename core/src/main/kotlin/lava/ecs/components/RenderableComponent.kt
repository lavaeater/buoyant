package lava.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import twodee.ecs.ashley.components.BodyPart

sealed class TypeOfRenderable {
    object Whatever: TypeOfRenderable()
    class RenderableCircle(var radius: Float, var color: Color = Color.WHITE, var filled: Boolean = false): TypeOfRenderable()
    class MultiSpritesForFixtures(val sprites: Map<BodyPart, Sprite>): TypeOfRenderable()
}


class RenderableComponent: Component, Pool.Poolable {
    var zIndex = 0
    var typeOfRenderable: TypeOfRenderable = TypeOfRenderable.Whatever

    override fun reset() {
        zIndex = 0
        typeOfRenderable = TypeOfRenderable.Whatever
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
