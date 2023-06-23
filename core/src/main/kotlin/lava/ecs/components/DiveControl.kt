package lava.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import ktx.math.vec2

class DiveControl: Component, Pool.Poolable {
    val diveForceAnchor = vec2()
    val directions = mutableSetOf<Direction>()
    var diveForce = 25f
    var isUnderWater = false
    var airSupply = 100f
    fun add(direction: Direction) {
        directions.add(direction)
    }

    fun remove(direction: Direction) {
        directions.remove(direction)
    }

    fun has(direction: Direction): Boolean {
        return directions.contains(direction)
    }

    fun hasAny(): Boolean {
        return directions.isNotEmpty()
    }

    private val directionVector = vec2()
    fun getVector(): Vector2 {
        directionVector.setZero()
        directions.forEach {
            directionVector.add(it.directionVector)
        }
        return directionVector
    }

    override fun reset() {
        diveForce = 25f
        airSupply = 100f
        directions.clear()
        diveForceAnchor.setZero()
    }

    companion object {
        val mapper = mapperFor<DiveControl>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }
        fun get(entity: Entity): DiveControl {
            return mapper.get(entity)
        }
    }
}
