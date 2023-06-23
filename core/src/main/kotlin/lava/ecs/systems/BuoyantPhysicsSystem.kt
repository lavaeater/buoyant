package lava.ecs.systems

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Shape
import com.badlogic.gdx.physics.box2d.World
import ktx.math.*
import lava.core.BuoyancySet
import twodee.ecs.ashley.systems.Box2dUpdateSystem
import kotlin.math.pow

class BuoyantPhysicsSystem(timeStep: Float, velIters: Int, posIters: Int, private val world: World) :
    Box2dUpdateSystem(timeStep, velIters, posIters) {
    override fun everyTimeStep(deltaTime: Float) {

        /*
        Our overlapping area is simple, actually, because our fixtures are all boxes or circles!

Our water is also just a horizontal line, very easy indeed.
         */

        for (contact in BuoyancySet.overlappingFixtures) {
            val intersectionData = findIntersection(contact.waterFixture, contact.buoyantFixture)
            if (intersectionData.under) {
                val displacedMass = contact.waterFixture.density * intersectionData.area
                val buoyancyForce = -world.gravity.cpy().scl(displacedMass)
                val bBody = contact.buoyantFixture.body
                bBody.applyForce(buoyancyForce, centroid, true)

                /**
                 * Do BETTER drag calculations, because these are indeed shit.
                 */

                val transformedVectors = intersectionData.polygon.transformedVectors()
                for(index in transformedVectors.indices step 2) {
                    val p0 = transformedVectors[index]
                    val p1 = if(index < transformedVectors.size - 1) transformedVectors[index + 1] else transformedVectors[0]
                    val midPoint = (p0 + p1) / 2f

                    val velDir = contact.buoyantFixture.body.getLinearVelocityFromWorldPoint(midPoint) -
                        contact.waterFixture.body.getLinearVelocityFromWorldPoint(midPoint)
                    val velocity = velDir.len()
                    velDir.nor()

                    val edge = p1 - p0
                    val edgeLength = edge.len()
                    edge.nor()
                    val normal = Vector2(-edge.y, edge.x)

                    val dragDot = normal.dot(velDir)
                    if(dragDot < 0f)
                        continue

                    val dragMag = dragDot * edgeLength * contact.waterFixture.density.pow(2) * velocity.pow(2)
                    val dragForce =-velDir.scl(dragMag)
                    contact.buoyantFixture.body.applyForce(dragForce, midPoint, true)

                    val angularDrag = intersectionData.area * -contact.buoyantFixture.body.angularVelocity
                    contact.buoyantFixture.body.applyTorque(angularDrag, true)
                }
            }
        }

    }

    private val waterVertices = mutableListOf<Vector2>()
    private val otherVertices = mutableListOf<Vector2>()
    private val centroid = vec2()
    fun findIntersection(waterFixture: Fixture, otherFixture: Fixture): IntersectionData {
        waterVertices.clear()
        otherVertices.clear()
        val waterShape = waterFixture.shape
        val otherShape = otherFixture.shape
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
            Shape.Type.Polygon -> {
                val polygonShape = otherShape as com.badlogic.gdx.physics.box2d.PolygonShape

                /*
                Since we have decided to have a **waterline** that is a horizontal line, we can
                simply create a new polygon that is the intersection of the waterline and the
                vertices
                 */

                for (i in 0 until polygonShape.vertexCount) {
                    val vertex = Vector2()
                    polygonShape.getVertex(i, vertex)
                    otherVertices.add(otherFixture.body.getWorldPoint(vertex).cpy())
                }

                val returnValue = if (otherVertices.minOf { it.y } > waterLine.first.y) {
                    //Entire polygon is above waterline
                    IntersectionData(Polygon(), vec2(), 0f, false)
                } else if (otherVertices.maxOf { it.y } < waterLine.first.y) {
                    //Entire polygon is below waterline
                    val polygon = Polygon(otherVertices.flatMap { listOf(it.x, it.y) }.toFloatArray())
                    IntersectionData(polygon, polygon.getCentroid(centroid), polygon.area(), true)
                } else {
                    val polygon = Polygon(otherVertices.flatMap { listOf(it.x, it.y) }.toFloatArray())
                    val intersectionPolygon = polygon.intersectedPolygon(waterLine.first, waterLine.second)
                    IntersectionData(intersectionPolygon, intersectionPolygon.getCentroid(centroid), intersectionPolygon.area(), true)
                }
                return returnValue
            }

            else -> {
                throw Exception("Unsupported shape type: ${otherShape.type}")
            }
        }
    }
}

fun Polygon.transformedVectors(): List<Vector2> {
    val vectors = mutableListOf<Vector2>()
    for (index in transformedVertices.indices step 2) {
        vectors.add(Vector2(transformedVertices[index], transformedVertices[index + 1]))
    }
    return vectors
}

fun Polygon.intersectedPolygon(lineStart: Vector2, lineEnd: Vector2): Polygon {
    val intersectionPoints = mutableListOf<Vector2>()
    for (i in transformedVertices.indices step 2) {
        val currentVertex = Vector2(transformedVertices[i], transformedVertices[i + 1])
        val nextVertex = Vector2(
            transformedVertices[(i + 2) % transformedVertices.size],
            transformedVertices[(i + 3) % transformedVertices.size]
        )

        val intersection = Vector2()
        if (Intersector.intersectSegments(lineStart, lineEnd, currentVertex, nextVertex, intersection)) {
            intersectionPoints.add(intersection.cpy())
        }
    }

    // What to do now? Remove all points ABOVE the waterline
    val vectors = this.transformedVectors()
    val belowPoints = vectors.filter { it.y < lineStart.y }
    intersectionPoints.addAll(belowPoints)
    /*
    Order the points
     */
    intersectionPoints.sortClockWise(this.getCentroid(vec2()))

    return Polygon(intersectionPoints.flatMap { listOf(it.x, it.y) }.toFloatArray())
}

fun Polygon.inside(other: Polygon): Boolean {
    for (i in 0 until this.vertexCount) {
        val vertex = vec2()
        this.getVertex(i, vertex)
        if (!other.contains(vertex)) {
            return false
        }
    }
    return true
}

fun MutableList<Vector2>.sortClockWise(centroid: Vector2): MutableList<Vector2> {
    this.sortWith { o1, o2 ->
        val slope1 = o1.slope(centroid)
        val slope2 = o2.slope(centroid)
        if (slope1 < slope2) -1 else 1
    }
    return this
}

fun Vector2.slope(reference: Vector2): Float {
    val dx = this.x - reference.x
    val dy = this.y - reference.y
    if (dx == 0f) {
        return if (dy >= 0f) Float.POSITIVE_INFINITY else Float.NEGATIVE_INFINITY
    }
    return dy / dx
}
