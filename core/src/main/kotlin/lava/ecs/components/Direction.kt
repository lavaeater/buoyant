package lava.ecs.components

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

sealed class Direction(val name: String, val directionVector: Vector2) {
    object Up: Direction("up", vec2(0f, -1f))
    object Down: Direction("down", vec2(0f, 1f))
    object Left: Direction("left", vec2(1f, 0f))
    object Right: Direction("right", vec2(-1f, 0f))
}
