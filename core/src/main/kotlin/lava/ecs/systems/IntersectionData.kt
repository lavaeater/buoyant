package lava.ecs.systems

import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2

data class IntersectionData(val polygon: Polygon, val centroid: Vector2, val area: Float, val under: Boolean = true)
