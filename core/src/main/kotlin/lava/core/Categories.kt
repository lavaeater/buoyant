package lava.core

import kotlin.experimental.or

object Categories {

    const val walls: Short = 1
    const val bodies: Short = 2
    const val extremities: Short = 4
    const val water: Short = 8

    val whatWallsCollideWith: Short = bodies or extremities
    val whatBodiesCollideWith: Short = walls or bodies or water
    val whatExtremitiesCollideWith: Short = walls or extremities or water
    val whatWaterCollidesWith: Short = bodies or extremities
}
