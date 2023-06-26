package lava.ecs.components

import box2dLight.ConeLight
import box2dLight.RayHandler
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import twodee.injection.InjectionContext.Companion.inject

class FlashlightComponent: Component, Pool.Poolable {
    private val rayHandler by lazy { inject<RayHandler>() }
    val flashLight = ConeLight(rayHandler, 64, Color(.2f,.2f,.2f,1f),10f,0f,0f,45f, 20f)
    override fun reset() {
    }

    companion object {
        val mapper = mapperFor<FlashlightComponent>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): FlashlightComponent {
            return mapper.get(entity)
        }
    }
}

