package lava.core

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.ConvexHull
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.ashley.entity
import ktx.ashley.with
import ktx.box2d.*
import ktx.math.minus
import ktx.math.plus
import ktx.math.vec2
import lava.ecs.components.*
import twodee.core.world
import twodee.ecs.ashley.components.Box2d
import twodee.ecs.ashley.components.CameraFollow
import twodee.ecs.ashley.components.LDtkMap
import twodee.ecs.ashley.components.TransformComponent
import twodee.injection.InjectionContext

class EntityFactory(
    private val engine: Engine,
    private val world: World,
    private val assets: Assets
) {

    fun createPlayerEntity(startPoint: Vector2, width: Float, height: Float) {
        engine.entity {
            with<RenderableComponent> {
                zIndex = 0
            }
            with<TransformComponent>()
            with<CameraFollow>()
            with<Box2d> {
                body = world.body {
                    type = com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
                    position.set(startPoint)
                    userData = this@entity.entity
                    fixedRotation = false
                    angularDamping = 0.5f
                    box(width, height) {
                        density = 0.1f
                        userData = "body"
                        filter {
                            categoryBits = Categories.bodies
                            maskBits = Categories.whatBodiesCollideWith
                        }
                    }
                    circle(width / 2f, vec2(0f, height / 2f + width / 2f)) {
                        density = 0.05f
                        userData = "head"
                        filter {
                            categoryBits = Categories.head
                            maskBits = Categories.whatHeadsCollideWith
                        }

                    }
                }
                bodies["body"] = body
                bodies["legs"] = world.body {
                    type = com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
                    position.set(startPoint + vec2(0f, height - width / 2f))
                    angularDamping = 0.5f
                    box(width, height * 1.5f, vec2(0f, 0f)) {
                        userData = "legs"
                        density = 1f
                        filter {
                            categoryBits = Categories.bodies
                            maskBits = Categories.whatBodiesCollideWith
                        }
                    }
                }
                body.revoluteJointWith(bodies["legs"]!!) {
                    localAnchorA.set(0f, -height / 2f)
                    localAnchorB.set(0f, height * 1.5f / 2f)
                    collideConnected = false
                    enableLimit = true
                    lowerAngle = MathUtils.degreesToRadians * -25f
                    upperAngle = MathUtils.degreesToRadians * 10f
                }
            }
            with<DiveControl> {
                diveForce = 50f
                diveForceAnchor.set(0f, height / 2f + height / 3f)
            }
        }
    }

    fun createWaterEntity(points: List<Vector2>) {
        engine.entity {
            with<WaterComponent>()
            with<PolygonComponent> {
                this.points.addAll(points)
            }
            with<Box2d> {
                body = world.body {
                    type = BodyDef.BodyType.StaticBody
                    position.set(0f, 0f)
                    polygon(*points.toTypedArray()) {
                        isSensor = true
                        density = 0.5f
                        filter {
                            categoryBits = Categories.water
                            maskBits = Categories.whatWaterCollidesWith
                        }
                    }

                }
            }

            with<RenderableComponent> {
                zIndex = 1
            }
        }
    }

    fun createMap(key: String): LDtkMap {
        var scaleFactor = 1f
        if (key == "two")
            scaleFactor = 1f
        val gridSize = 8f * scaleFactor
        val mapOffset = vec2(-50f, -50f)
        val mapAssets = assets().maps[key]!!
        val textureRegion = TextureRegion(mapAssets.first)
        lateinit var LDtkMap: LDtkMap
        engine.entity {
            with<RenderableComponent> {
                zIndex = -1
            }
            LDtkMap = with<LDtkMap> {
                this.gridSize = gridSize
                mapTextureRegion = textureRegion
                mapRotation = -25f
                mapScale = scaleFactor
                mapOrigin.set(mapOffset)
                mapBounds = Rectangle(
                    mapOffset.x + gridSize,
                    mapOffset.y + gridSize,
                    textureRegion.regionWidth.toFloat() - 2 * gridSize,
                    textureRegion.regionHeight.toFloat() - 2 * gridSize
                )
            }
            addPoints(mapAssets.second, gridSize, mapOffset, LDtkMap)
            createBounds(gridSize, mapOffset, LDtkMap)
            createWater(gridSize, mapOffset, LDtkMap)
            createPlayerEntity(
                LDtkMap.points[TypeOfPoint.PlayerStart]!!.random()
                    .rotateAroundDeg(LDtkMap.mapOrigin, LDtkMap.mapRotation), 1f, 2.5f
            )
        }
        return LDtkMap
    }

    private fun addPoints(intLayer: String, tileSize: Float, mapOffset: Vector2, lDtkMap: LDtkMap) {
        intLayer.lines().reversed().forEachIndexed { y, l ->
            l.split(',').forEachIndexed { x, c ->
                if (TypeOfPoint.allTypes.containsKey(c)) {
                    val pointType = TypeOfPoint.allTypes[c]!!
                    if (!lDtkMap.points.containsKey(pointType)) {
                        lDtkMap.points[pointType] = mutableListOf()
                    }
                    lDtkMap.points[pointType]!!.add(
                        vec2(
                            x * tileSize + mapOffset.x + tileSize / 2f,
                            y * tileSize + mapOffset.y - tileSize / 2f
                        )
                    )
                }
            }
        }
    }

    fun createWater(tileSize: Float, mapOffset: Vector2, lDtkMap: LDtkMap) {

        /**
         * We want to create shared edges. Oh yeah!
         *
         * We create a bunch of rectangles, then we get to work!
         *
         */
        val offset = tileSize / 2f

        val topLeft = vec2(-offset, offset)//.apply { rotateDeg(lDtkMap.mapRotation) }
        val topRight = vec2(offset, offset)//.apply { rotateDeg(lDtkMap.mapRotation) }
        val bottomRight = vec2(offset, -offset)//.apply { rotateDeg(lDtkMap.mapRotation) }
        val bottomLeft = vec2(-offset, -offset)//.apply { rotateDeg(lDtkMap.mapRotation) }
        lDtkMap.points[TypeOfPoint.BlobStart]!!.forEach { water ->
            val points = listOf(
                vec2(water.x + topLeft.x, water.y + topLeft.y),
                vec2(water.x + topRight.x, water.y + topRight.y),
                vec2(water.x + bottomRight.x, water.y + bottomRight.y),
                vec2(water.x + bottomLeft.x, water.y + bottomLeft.y)
            )

            for (point in points) {
                point.rotateAroundDeg(lDtkMap.mapOrigin, lDtkMap.mapRotation)
            }

            createWaterEntity(points)
        }
    }


    fun createBounds(tileSize: Float, mapOffset: Vector2, lDtkMap: LDtkMap) {
        /*
        To make it super easy, we just create a square per int-tile in the layer.
         */
        for (bound in lDtkMap.points[TypeOfPoint.Impassable]!!) {
            bound.rotateAroundDeg(lDtkMap.mapOrigin, lDtkMap.mapRotation)
            lDtkMap.mapBodies.add(world.body {
                type = BodyDef.BodyType.StaticBody
                position.set(
                    bound.x,
                    bound.y
                )
                angle = lDtkMap.mapRotation.toRadians()
                box(tileSize, tileSize) {
                    filter {
                        categoryBits = Categories.walls
                        maskBits = Categories.whatWallsCollideWith
                    }
                }
            })
        }
    }

    private var needsWaterLine = true
    private val waterLine = mutableListOf<Vector2>()

    fun getWaterLine(map: LDtkMap): List<Vector2> {
        if (needsWaterLine) {
            val bounds = map.points[TypeOfPoint.Impassable]!!
            val topLeft = bounds.maxBy { it.y }
            val topRight = bounds.maxBy { it.x }
            val bottomLeft = bounds.minBy { it.x }
            val bottomRight = bounds.minBy { it.y }

            val topMiddle = topLeft - (topLeft - topRight).scl(0.5f)
            val bottomMiddle = bottomLeft - (bottomLeft - bottomRight).scl(0.5f)

            val eighty = topMiddle + (bottomMiddle - topMiddle).scl(0.10f)

            waterLine.add(topLeft)
            waterLine.add(topRight)


            val toAdd = vec2(map.gridSize / 2f, map.gridSize / 2f)
            toAdd.rotateDeg(map.mapRotation)

            bottomLeft.add(toAdd)

            waterLine.add(bottomLeft)
            toAdd.set(-map.gridSize / 2f, map.gridSize / 2f)
            toAdd.rotateDeg(map.mapRotation)
            bottomRight.add(toAdd)

            waterLine.add(bottomRight)

            waterLine.add(topMiddle)
            waterLine.add(bottomMiddle)
            waterLine.add(eighty)

            /*
            RayCAST
             */

            val rightWall = vec2()
            val leftWall = vec2()

            var lastFraction = 1f
            world().rayCast(eighty, eighty + Vector2.X.cpy().scl(100f)) { fixture, point, normal, fraction ->
                if (fraction < lastFraction) {
                    lastFraction = fraction
                    rightWall.set(point)
                }
                RayCast.CONTINUE
            }
            waterLine.add(rightWall)
            lastFraction = 1f
            world().rayCast(rightWall, rightWall + Vector2.X.cpy().scl(-100f)) { fixture, point, normal, fraction ->
                if (fraction < lastFraction) {
                    lastFraction = fraction
                    leftWall.set(point)
                }
                RayCast.CONTINUE
            }
            waterLine.add(leftWall)

            InjectionContext.inject<EntityFactory>()
                .createWaterEntity(listOf(leftWall, rightWall, bottomRight, bottomLeft))
            InjectionContext.inject<EntityFactory>().createPlayerEntity(eighty, 1f, 2.5f)
//            inject<EntityFactory>().createPlayerEntity(vec2(eighty.x, eighty.y - 0.5f), 1f, 2.5f)

            needsWaterLine = false
        }
        return waterLine
    }
}

fun Float.toRadians(): Float {
    return this * MathUtils.degreesToRadians
}

fun Int.factorDivisor(range: IntRange): Int {
    for (divisor in range) {
        if (this % divisor == 0)
            return divisor
    }
    return -1
}


