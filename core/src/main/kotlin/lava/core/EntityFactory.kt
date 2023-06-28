package lava.core

import box2dLight.ConeLight
import box2dLight.RayHandler
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.ashley.entity
import ktx.ashley.with
import ktx.box2d.*
import ktx.math.plus
import ktx.math.vec2
import lava.ecs.components.*
import twodee.ecs.ashley.components.*
import twodee.injection.InjectionContext.Companion.inject


class EntityFactory(
    private val engine: Engine,
    private val world: World,
    private val assets: Assets
) {

    fun createPlayerEntity(startPoint: Vector2, width: Float, height: Float) {
        engine.entity {
            with<RenderableComponent> {
                zIndex = 0
                typeOfRenderable = TypeOfRenderable.MultiSpritesForFixtures(
                    mapOf(
                        BodyPart.Head to assets.bodyParts[BodyPart.Head]!!,
                        BodyPart.Body to assets.bodyParts[BodyPart.Body]!!,
                        BodyPart.Legs to assets.bodyParts[BodyPart.Legs]!!
                    )
                )
            }
            with<TransformComponent>()
            //Get back to bubbles later, mate!
            with<BubbleEmitterComponent>()
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
                        userData = BodyPart.Body
                        filter {
                            categoryBits = Categories.bodies
                            maskBits = Categories.whatBodiesCollideWith
                        }
                    }
                    circle(width / 2f, vec2(0f, height / 2f + width / 2f)) {
                        density = 0.05f
                        userData = BodyPart.Head
                        filter {
                            categoryBits = Categories.head
                            maskBits = Categories.whatHeadsCollideWith
                        }

                    }
                }
                bodies[BodyPart.Body] = body
                bodies[BodyPart.Legs] = world.body {
                    type = com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
                    position.set(startPoint + vec2(0f, height - width / 2f))
                    angularDamping = 0.5f
                    box(width, height * 1.5f, vec2(0f, 0f)) {
                        userData = BodyPart.Legs
                        density = 1f
                        filter {
                            categoryBits = Categories.bodies
                            maskBits = Categories.whatBodiesCollideWith
                        }
                    }
                }
                body.revoluteJointWith(bodies[BodyPart.Legs]!!) {
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
            with<FlashlightComponent> {
                flashLight.setContactFilter(Categories.light, 0, Categories.whatLightCollidesWith)
            }
        }
    }

    fun createWinArea(points: List<Vector2>) {
        engine.entity {
            with<Box2d> {
                body = world.body {
                    type = BodyDef.BodyType.StaticBody
                    position.set(0f, 0f)
                    polygon(*points.toTypedArray()) {
                        isSensor = true
                        density = 0.5f
                        filter {
                            categoryBits = Categories.winArea
                            maskBits = Categories.whatWinAreaCollidesWith
                        }
                    }

                }
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
            createWinArea(gridSize, mapOffset, LDtkMap)


            val startLight = LDtkMap.points[TypeOfPoint.PlayerStart]!!.first().cpy().add(-gridSize / 2f, gridSize / 2f)
                .rotateAroundDeg(LDtkMap.mapOrigin, LDtkMap.mapRotation)
//
//            PointLight(inject<RayHandler>(), 32, Color.RED,15f,startLight.x, startLight.y).apply {
//                isActive = true
//            }

            ConeLight(inject<RayHandler>(), 32, Color.RED, 25f, startLight.x, startLight.y, -90f, 20f).apply {
                isActive = true
            }


            createPlayerEntity(
                LDtkMap.points[TypeOfPoint.PlayerStart]!!.first()
                    .rotateAroundDeg(LDtkMap.mapOrigin, LDtkMap.mapRotation), 1f, 2.5f
            )
        }
        return LDtkMap
    }

    private fun createWinArea(gridSize: Float, mapOffset: Vector2, lDtkMap: LDtkMap) {
        val offset = gridSize / 2f

        val topLeft = vec2(-offset, offset)//.apply { rotateDeg(lDtkMap.mapRotation) }
        val topRight = vec2(offset, offset)//.apply { rotateDeg(lDtkMap.mapRotation) }
        val bottomRight = vec2(offset, -offset)//.apply { rotateDeg(lDtkMap.mapRotation) }
        val bottomLeft = vec2(-offset, -offset)//.apply { rotateDeg(lDtkMap.mapRotation) }
        (lDtkMap.points[TypeOfPoint.Lights]!!).forEach { winArea ->
            val points = listOf(
                vec2(winArea.x + topLeft.x, winArea.y + topLeft.y),
                vec2(winArea.x + topRight.x, winArea.y + topRight.y),
                vec2(winArea.x + bottomRight.x, winArea.y + bottomRight.y),
                vec2(winArea.x + bottomLeft.x, winArea.y + bottomLeft.y)
            )

            for (point in points) {
                point.rotateAroundDeg(lDtkMap.mapOrigin, lDtkMap.mapRotation)
            }

            winArea.add(topLeft).rotateAroundDeg(lDtkMap.mapOrigin, lDtkMap.mapRotation)
            val angle = (-90..-45 step 10).toList().random().toFloat()
            ConeLight(inject<RayHandler>(), 16, Color(0f, 0.8f, 0f, 0.7f), 50f, winArea.x, winArea.y, angle, 30f).apply {
                isActive = true
            }

            createWinArea(points)
        }

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
        (lDtkMap.points[TypeOfPoint.BlobStart]!!).forEach { water ->
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
        (lDtkMap.points[TypeOfPoint.HumanStart]!!).forEach { water ->
            val points = listOf(
                vec2(water.x + topLeft.x, water.y + topLeft.y),
                vec2(water.x + topRight.x, water.y + topRight.y),
                vec2(water.x + bottomRight.x, water.y + bottomRight.y),
                vec2(water.x + bottomLeft.x, water.y + bottomLeft.y)
            )

            for (point in points) {
                point.rotateAroundDeg(lDtkMap.mapOrigin, lDtkMap.mapRotation)
            }

            water.add(-tileSize / 2f, tileSize / 2f).rotateAroundDeg(lDtkMap.mapOrigin, lDtkMap.mapRotation)
            val angle = (-90..-45 step 10).toList().random().toFloat()
            ConeLight(inject<RayHandler>(), 16, Color(0.8f, 0f, 0f, 0.7f), 50f, water.x, water.y, angle, 30f).apply {
                isActive = true
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

    fun emitBubble(startPoint: Vector2, radius: Float) {
        engine.entity {
            with<RenderableComponent> {
                zIndex = 0
                typeOfRenderable = TypeOfRenderable.RenderableCircle(radius)
            }
            with<TransformComponent>()
            with<Box2d> {
                body = world.body {
                    type = com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
                    position.set(startPoint)
                    userData = this@entity.entity
                    fixedRotation = false
                    angularDamping = 0.5f
                    linearDamping = 1f
                    gravityScale = 0.25f
                    circle(radius) {
                        density = 2f
                        filter {
                            categoryBits = Categories.bubbles
                            maskBits = Categories.whatBubblesCollideWith
                        }
                    }
                }
            }
            with<BubbleComponent> {
                this.radius = radius
            }
        }
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


