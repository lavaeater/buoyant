package lava.ecs.components

import box2dLight.PointLight
import box2dLight.RayHandler
import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pool
import twodee.injection.InjectionContext.Companion.inject

class LightComponent : Component, Pool.Poolable {
    val light = PointLight(inject<RayHandler>(),32, Color.RED, 2.5f, 0f, 0f).apply {
        isActive = false
    }
    override fun reset() {
        light.isActive = false
    }
}

