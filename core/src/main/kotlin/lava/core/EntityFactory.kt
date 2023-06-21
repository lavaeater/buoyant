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
import ktx.box2d.filter
import ktx.math.vec2
import twodee.ai.ashley.angleToDeg
import twodee.ecs.ashley.components.LDtkMap

class EntityFactory(
    private val engine: Engine,
    private val world: World,
    private val assets: Assets) {
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
            LDtkMap = with<LDtkMap> {
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

