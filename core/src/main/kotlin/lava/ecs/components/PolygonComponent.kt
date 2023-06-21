package lava.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class PolygonComponent : Component, Pool.Poolable {
    val points = mutableListOf<Vector2>()
    val floats get() = points.flatMap { listOf(it.x, it.y) }.toFloatArray()
    private var needsPolygon = true
    private var polygonCache = Polygon(floatArrayOf(0f,1f,2f,3f,4f,5f))
    val polygon: Polygon
        get() {
            if (needsPolygon && points.size > 2) {
                needsPolygon = false
                polygonCache = Polygon(floats)
            }
            return polygonCache
        }

    override fun reset() {
        points.clear()
        needsPolygon = true
    }

    companion object {
        val mapper = mapperFor<PolygonComponent>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }

        fun get(entity: Entity): PolygonComponent {
            return mapper.get(entity)
        }
    }
}
