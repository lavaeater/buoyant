package lava.core

import kotlin.experimental.or

object Categories {

    const val walls: Short = 1
    const val bodies: Short = 2
    const val head: Short = 4
    const val water: Short = 8
    const val light: Short = 16
    const val bubbles: Short = 32

    val whatWallsCollideWith: Short = bodies or head or light or bubbles
    val whatBodiesCollideWith: Short = walls or bodies or water or light
    val whatHeadsCollideWith: Short = walls or light
    val whatWaterCollidesWith: Short = bodies or head or bubbles
    val whatLightCollidesWith: Short = bodies or head or walls or bubbles
    val whatBubblesCollideWith: Short = walls or light or water
}
