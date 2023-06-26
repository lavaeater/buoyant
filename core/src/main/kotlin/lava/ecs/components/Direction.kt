package lava.ecs.components

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

sealed class Direction(val name: String, val directionVector: Vector2) {
    object Swim: Direction("up", vec2(0f, 1f))
    object Hover: Direction("down", vec2(0f, 0f))
    object RotateLeft: Direction("left", vec2(-1f, 0f))
    object RotateRight: Direction("right", vec2(1f, 0f))
}
