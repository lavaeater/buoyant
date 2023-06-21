package lava.ecs.systems

import box2dLight.RayHandler
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import ktx.ashley.allOf
import ktx.assets.toInternalFile
import ktx.graphics.use
import lava.core.GameSettings
import space.earlygrey.shapedrawer.ShapeDrawer
import twodee.ecs.ashley.components.LDtkMap


class RenderSystem(
    private val batch: PolygonSpriteBatch,
    private val shapeDrawer: ShapeDrawer,
    private val camera: OrthographicCamera,
    private val gameSettings: GameSettings,
    private val rayHandler: RayHandler
) : EntitySystem() {
//    private val shaderProgram by lazy {
//        val vertexShader = "shaders/vertex.glsl".toInternalFile().readString()
//        val fragmentShader = "shaders/fragment.glsl".toInternalFile().readString()
//        ShaderProgram.pedantic = false
//        ShaderProgram(vertexShader, fragmentShader)
//    }
    private val LDtkMapFamily = allOf(LDtkMap::class).get()
    private val mapEntity get() = engine.getEntitiesFor(LDtkMapFamily).first() //Should always be one

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
            renderMap()
        }
//        rayHandler.setCombinedMatrix(camera)
//        rayHandler.updateAndRender()
    }

    private fun renderMap() {
        val lDtkMap = LDtkMap.get(mapEntity)
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
