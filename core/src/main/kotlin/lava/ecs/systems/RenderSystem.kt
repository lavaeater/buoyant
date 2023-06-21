package lava.ecs.systems

import box2dLight.RayHandler
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Vector2
import ktx.ashley.allOf
import ktx.graphics.use
import ktx.math.minus
import ktx.math.plus
import ktx.box2d.*
import ktx.math.vec2
import lava.core.EntityFactory
import lava.core.GameSettings
import lava.core.TypeOfPoint
import lava.ecs.components.PolygonComponent
import lava.ecs.components.RenderableComponent
import space.earlygrey.shapedrawer.ShapeDrawer
import twodee.core.world
import twodee.ecs.ashley.components.LDtkMap
import twodee.injection.InjectionContext.Companion.inject


class RenderSystem(
    private val batch: PolygonSpriteBatch,
    private val shapeDrawer: ShapeDrawer,
    private val camera: OrthographicCamera,
    private val gameSettings: GameSettings,
    private val rayHandler: RayHandler
) : SortedIteratingSystem(allOf(RenderableComponent::class).get(), compareBy {
    RenderableComponent.get(it).zIndex }
) {
//    private val shaderProgram by lazy {
//        val vertexShader = "shaders/vertex.glsl".toInternalFile().readString()
//        val fragmentShader = "shaders/fragment.glsl".toInternalFile().readString()
//        ShaderProgram.pedantic = false
//        ShaderProgram(vertexShader, fragmentShader)
//    }
    private val LDtkMapFamily = allOf(LDtkMap::class).get()

    private val fbo by lazy {
        FrameBuffer(
            Pixmap.Format.RGBA8888,
            Gdx.graphics.width * 2,
            Gdx.graphics.height * 2,
            true
        )
    }

    override fun update(deltaTime: Float) {
        //fbo.begin()
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        camera.update(true)
        batch.projectionMatrix = camera.combined
        batch.use {
            super.update(deltaTime)
        }
//        rayHandler.setCombinedMatrix(camera)
//        rayHandler.updateAndRender()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if(LDtkMap.has(entity)) {
            renderMap(entity)
//            renderDebugStuff(entity)
        }
        if(PolygonComponent.has(entity)) {
            renderPolygon(entity)
        }
    }

    val polygonColor = Color(0.5f, 0.5f, 0.5f, 0.5f)

    private fun renderPolygon(entity: Entity) {
        val polygonComponent = PolygonComponent.get(entity)
        shapeDrawer.setColor(polygonColor)
        shapeDrawer.filledPolygon(polygonComponent.polygon)
        shapeDrawer.setColor(Color.WHITE)
    }

    private var needsWaterLine = true
    private val waterLine = mutableListOf<Vector2>()

    fun getWaterLine(map: LDtkMap): List<Vector2> {
        if(needsWaterLine) {
            val bounds = map.points[TypeOfPoint.Impassable]!!
            val topLeft = bounds.maxBy { it.y }
            val topRight = bounds.maxBy { it.x }
            val bottomLeft = bounds.minBy { it.x }
            val bottomRight = bounds.minBy { it.y }

            val topMiddle = topLeft - (topLeft - topRight).scl(0.5f)
            val bottomMiddle = bottomLeft - (bottomLeft - bottomRight).scl(0.5f)

            val eighty = topMiddle + ( bottomMiddle - topMiddle).scl(0.10f)

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
            world().rayCast(eighty, eighty + Vector2.X.cpy().scl(100f) ) { fixture, point, normal, fraction ->
                if(fraction < lastFraction) {
                    lastFraction = fraction
                    rightWall.set(point)
                }
                RayCast.CONTINUE
            }
            waterLine.add(rightWall)
            lastFraction = 1f
            world().rayCast(rightWall, rightWall + Vector2.X.cpy().scl(-100f) ) { fixture, point, normal, fraction ->
                if(fraction < lastFraction) {
                    lastFraction = fraction
                    leftWall.set(point)
                }
                RayCast.CONTINUE
            }
            waterLine.add(leftWall)

            inject<EntityFactory>().createWaterEntity(listOf(leftWall, rightWall, bottomRight, bottomLeft))
            inject<EntityFactory>().createPlayerEntity(eighty, 1f, 2.5f)

            needsWaterLine = false
        }
        return waterLine
    }

    private fun renderDebugStuff(mapEntity: Entity) {
        val lDtkMap = LDtkMap.get(mapEntity)
        for (bound in lDtkMap.points[TypeOfPoint.Impassable]!!) {
            shapeDrawer.filledCircle(bound, 1f, Color.RED)
        }
        for (point in getWaterLine(lDtkMap)) {
            shapeDrawer.filledCircle(point, 1f, Color.BLUE)
        }
        if(getWaterLine(lDtkMap).size == 2) {
            shapeDrawer.line(getWaterLine(lDtkMap).first(), getWaterLine(lDtkMap).last(), Color.BLUE)
        }
    }

    private fun renderMap(entity: Entity) {
        val lDtkMap = LDtkMap.get(entity)
        getWaterLine(lDtkMap)
        batch.draw(
            lDtkMap.mapTextureRegion,
            lDtkMap.mapOrigin.x,
            lDtkMap.mapOrigin.y,
            0f, 0f,
            lDtkMap.mapTextureRegion.regionWidth.toFloat(),
            lDtkMap.mapTextureRegion.regionHeight.toFloat(),
            lDtkMap.mapScale,
            lDtkMap.mapScale,
            lDtkMap.mapRotation
        )
    }

    private fun renderTopLayerMap() {
//        val LDtkMap = LDtkMap.get(mapEntity)
//        batch.draw(
//            LDtkMap.mapTopLayerRegion,
//            LDtkMap.mapOrigin.x,
//            LDtkMap.mapOrigin.y
//        )
    }

    private fun renderShader(deltaTime: Float) {
//        shaderTime - deltaTime
//        if (shaderTime < 0f)
//            shaderTime = 2f
        //        fbo.end()
//        batch.use {
//            batch.shader = shaderProgram
//            /**
//             * We need all the points
//             */
//
//            camera.project(blobCenter)
//
//            shaderCenter.set(blobCenter.x / Gdx.graphics.width, blobCenter.y / Gdx.graphics.height)
//            shaderProgram.setUniformf("time", deltaTime)
//            shaderProgram.setUniformf("center", shaderCenter)
//            shaderProgram.setUniformf("shockParams", shockParams)
//            val texture = fbo.colorBufferTexture
//            val textureRegion = TextureRegion(texture)
//            // and.... FLIP!  V (vertical) only
//            // and.... FLIP!  V (vertical) only
//            textureRegion.flip(false, true)
//            batch.draw(
//                textureRegion,
//                camera.position.x - camera.viewportWidth / 2f,
//                camera.position.y - camera.viewportHeight / 2f,
//                camera.viewportWidth,
//                camera.viewportHeight
//            )
//            batch.shader = null
//        }
    }
}
