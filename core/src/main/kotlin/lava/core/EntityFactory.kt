package lava.core

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import ktx.ashley.entity
import ktx.ashley.with
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.circle
import ktx.box2d.filter
import ktx.math.vec2
import lava.ecs.components.PolygonComponent
import lava.ecs.components.RenderableComponent
import lava.ecs.components.WaterComponent
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
                    box(width, height, vec2(0f, -height / 2)) {
                        filter {
                            categoryBits = Categories.bodies
                            maskBits = Categories.whatBodiesCollideWith
                        }
                    }
                    circle(width / 2, vec2(0f, width / 2)) {
                        filter {
                            categoryBits = Categories.extremities
                            maskBits = Categories.whatExtremitiesCollideWith
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


