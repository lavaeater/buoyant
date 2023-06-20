package lava.core

import kotlin.experimental.or

object Categories {

    const val walls: Short = 1
    const val people: Short = 2
    const val fish: Short = 4


    val whatWallsCollideWith: Short = people or fish
}
