package lava.core

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.TextureRegion
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
import twodee.ecs.ashley.components.LDtkMap
import twodee.ecs.ashley.components.PointType
import kotlin.experimental.or

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

object Categories {

    const val walls: Short = 1
    const val people: Short = 2
    const val fish: Short = 4


    val whatWallsCollideWith: Short = people or fish
}

class EntityFactory(
    private val engine: Engine,
    private val world: World,
    private val assets: Assets) {
    fun createMap(key: String): LDtkMap {
        var scaleFactor = 1f
        if (key == "two")
            scaleFactor = 2f
        val gridSize = 8f * scaleFactor
        val mapOffset = vec2(-50f, -50f)
        val mapAssets = assets().maps[key]!!
        val textureRegion = TextureRegion(mapAssets.first)
        val topTextureRegion = TextureRegion(mapAssets.third)
        lateinit var LDtkMap: LDtkMap
        engine.entity {
            LDtkMap = with {
                mapTextureRegion = textureRegion
                mapTopLayerRegion = topTextureRegion
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

    fun createBounds(intLayer: String, tileSize: Float, mapOffset: Vector2, LDtkMap: LDtkMap) {
        /*
        To make it super easy, we just create a square per int-tile in the layer.
         */
        intLayer.lines().reversed().forEachIndexed { y, l ->
            l.split(',').forEachIndexed { x, c ->
                if (TypeOfPoint.allTypes.containsKey(c)) {
                    val pointType = TypeOfPoint.allTypes[c]!!
                    if (!LDtkMap.points.containsKey(pointType)) {
                        LDtkMap.points[pointType] = mutableListOf()
                    }
                    LDtkMap.points[pointType]!!.add(
                        vec2(
                            x * tileSize + mapOffset.x + tileSize / 2f,
                            y * tileSize + mapOffset.y - tileSize / 2f
                        )
                    )
                }
            }
        }

        for (bound in LDtkMap.points[TypeOfPoint.Impassable]!!) {
            LDtkMap.mapBodies.add(world.body {
                type = BodyDef.BodyType.StaticBody
                position.set(
                    bound.x,
                    bound.y
                )
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


