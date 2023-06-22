package lava.ecs.systems

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Shape
import ktx.math.vec2
import twodee.ecs.ashley.systems.Box2dUpdateSystem
import kotlin.math.pow
import kotlin.math.sqrt

data class IntersectionData(val centroid: Vector2, val area: Float, val under: Boolean = true)

class BuoyantPhysicsSystem(timeStep: Float, velIters: Int, posIters: Int) :
    Box2dUpdateSystem(timeStep, velIters, posIters) {
    override fun everyTimeStep(deltaTime: Float) {

        /*
        Our overlapping area is simple, actually, because our fixtures are all boxes or circles!

Our water is also just a horizontal line, very easy indeed.
         */


    }

    fun findIntersection(waterFixture: Fixture, otherFixture: Fixture): IntersectionData {
        val waterShape = waterFixture.shape
        val otherShape = otherFixture.shape
        val waterVertices = mutableListOf<Vector2>()
        val otherVertices = mutableListOf<Vector2>()
        var waterLine = Pair(vec2(-1000f, 0f), vec2(1000f, 0f))
        when (waterShape.type) {
            com.badlogic.gdx.physics.box2d.Shape.Type.Polygon -> {
                val polygonShape = waterShape as com.badlogic.gdx.physics.box2d.PolygonShape
                for (i in 0 until polygonShape.vertexCount) {
                    val vertex = Vector2()
                    polygonShape.getVertex(i, vertex)
                    waterVertices.add(vertex)
                }

                // Find maxY
                val maxY = waterVertices.maxBy { it.y }.y
                waterLine = Pair(vec2(-1000f, maxY), vec2(1000f, maxY))
            }

            else -> {
                throw Exception("Unsupported shape type: ${waterShape.type}")
            }
        }

        when (otherShape.type) {
//            Shape.Type.Circle -> {
//                /**
//                 * Area is the sector MINUS the triangle formed by the center of the circle and the two points of intersection.
//                 */
//                val circleShape = otherShape as com.badlogic.gdx.physics.box2d.CircleShape
//                val circleCenter = circleShape.position
//                val circleRadius = circleShape.radius
//
//                val dX = waterLine.second.x - waterLine.first.x
//                val dY = waterLine.second.y - waterLine.first.y
//                val dR = sqrt(dX.pow(2) + dY.pow(2))
//                val bigD = waterLine.first.x * waterLine.second.y - waterLine.second.x * waterLine.first.y
//
//                val discriminant = circleRadius.pow(2) * dR.pow(2) - bigD.pow(2)
//                if(discriminant <= 0f) {
//                    val circleMinY = circleCenter.y - circleRadius
//                    return if(circleMinY < waterLine.first.y) {
//                        IntersectionData(circleCenter.cpy(), circleRadius.pow(2) * MathUtils.PI)
//                    } else {
//                        IntersectionData(vec2(), 0f, false)
//                    }
//                } else {
//
//                    val x1 = (bigD * dY + (dY) * dX * sqrt(discriminant)) / dR.pow(2)
//                    val y1 = (-bigD * dX + (dY) * dY * sqrt(discriminant)) / dR.pow(2)
//
//
//                    /**
//                     * This is so much easier than I am thinking it is - a circle intersecting a line can be thought of
//                     * as a sector where we know... something
//                     *
//                     */
//
//
//                }
//
//            }
            Shape.Type.Polygon -> {
                val polygonShape = otherShape as com.badlogic.gdx.physics.box2d.PolygonShape
                for (i in 0 until polygonShape.vertexCount) {
                    val vertex = Vector2()
                    polygonShape.getVertex(i, vertex)
                    otherVertices.add(vertex)
                }
            }

            else -> {
                throw Exception("Unsupported shape type: ${otherShape.type}")
            }
        }
    }
}

fun Polygon.inside(other:Polygon): Boolean {
    for(i in 0 until this.vertexCount) {
        val vertex = vec2()
        this.getVertex(i, vertex)
        if(!other.contains(vertex)) {
            return false
        }
    }
    return true
}
