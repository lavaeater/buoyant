package lava.core

import twodee.ecs.ashley.components.PointType

sealed class TypeOfPoint(override val character: String) : PointType {
    object BlobStart: TypeOfPoint("2")
    object PlayerStart: TypeOfPoint("3")
    object HumanStart: TypeOfPoint("4")
    object Lights: TypeOfPoint("5")
    object Impassable: TypeOfPoint("1")

    companion object {
        val allTypes = listOf(BlobStart, PlayerStart, HumanStart, Lights, Impassable).associateBy { it.character }
    }
}
