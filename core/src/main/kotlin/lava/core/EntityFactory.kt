package lava.core

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.ashley.entity
import ktx.ashley.with
import ktx.box2d.*
import ktx.math.plus
import ktx.math.vec2
import lava.ecs.components.*
import twodee.ecs.ashley.components.Box2d
import twodee.ecs.ashley.components.CameraFollow
import twodee.ecs.ashley.components.LDtkMap
import twodee.ecs.ashley.components.TransformComponent

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
                    localAnchorA.set(0f, -height /2f)
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
                        density = 1f
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
            createBounds(mapAssets.second, gridSize, mapOffset, LDtkMap)
        }
        return LDtkMap
    }

    fun createBounds(intLayer: String, tileSize: Float, mapOffset: Vector2, lDtkMap: LDtkMap) {
        /*
        To make it super easy, we just create a square per int-tile in the layer.
         */
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

        for (bound in lDtkMap.points[TypeOfPoint.Impassable]!!) {
            bound.rotateAroundDeg(lDtkMap.mapOrigin, lDtkMap.mapRotation)
        }

        for (bound in lDtkMap.points[TypeOfPoint.Impassable]!!) {
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
}

private fun Float.toRadians(): Float {
    return this * MathUtils.degreesToRadians
}


