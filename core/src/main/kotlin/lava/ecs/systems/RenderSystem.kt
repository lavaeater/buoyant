package lava.ecs.systems

import box2dLight.RayHandler
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ktx.ashley.allOf
import ktx.graphics.use
import ktx.math.minus
import ktx.math.plus
import ktx.box2d.*
import ktx.math.vec2
import lava.core.Assets
import lava.core.EntityFactory
import lava.core.GameSettings
import lava.core.TypeOfPoint
import lava.ecs.components.DiveControl
import lava.ecs.components.PolygonComponent
import lava.ecs.components.RenderableComponent
import lava.ecs.components.TypeOfRenderable
import space.earlygrey.shapedrawer.ShapeDrawer
import twodee.core.world
import twodee.ecs.ashley.components.BodyPart
import twodee.ecs.ashley.components.Box2d
import twodee.ecs.ashley.components.LDtkMap
import twodee.injection.InjectionContext.Companion.inject


class RenderSystem(
    private val batch: PolygonSpriteBatch,
    private val shapeDrawer: ShapeDrawer,
    private val camera: OrthographicCamera,
    private val gameSettings: GameSettings,
    private val rayHandler: RayHandler,
    private val assets: Assets
) : SortedIteratingSystem(allOf(RenderableComponent::class).get(), compareBy {
    RenderableComponent.get(it).zIndex
}
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
        if (LDtkMap.has(entity)) {
            renderMap(entity)
//            renderDebugStuff(entity)
        }
        if (PolygonComponent.has(entity)) {
            renderPolygon(entity)
        }
        if (DiveControl.has(entity)) {
            renderDiveControl(entity)
        }
        if (RenderableComponent.get(entity).typeOfRenderable is TypeOfRenderable.MultiSpritesForFixtures) {
            renderMultiSprites(entity)
        }
    }

    private fun renderMultiSprites(entity: Entity) {
        val sprites =
            (RenderableComponent.get(entity).typeOfRenderable as TypeOfRenderable.MultiSpritesForFixtures).sprites
        val box2d = Box2d.get(entity)
        val bodies = box2d.bodies
        val allFixtures = box2d.bodies.values.flatMap { it.fixtureList }.associateBy { (it.userData as BodyPart) }
        for ((part, fixture) in allFixtures) {
            val sprite = sprites[part]!!
            val body = fixture.body
            val position = body.getWorldPoint(fixture.shape.getPosition()) - vec2(sprite.regionWidth.toFloat() / 2f, sprite.regionHeight.toFloat() / 2f)//.rotateRad(body.angle)
            val angle = body.angle
            sprite.setPosition(position.x, position.y)
            sprite.rotation = angle * MathUtils.radiansToDegrees
            sprite.setOriginCenter()
            sprite.setScale(gameSettings.MetersPerPixel)
            sprite.draw(batch)

        }
    }

    private fun renderDiveControl(entity: Entity) {
        val diveControl = DiveControl.get(entity)
        val box2d = Box2d.get(entity)
        val body = box2d.body
        shapeDrawer.line(body.position, body.position + diveControl.diveVector.cpy().scl(2.5f), Color.RED)
        shapeDrawer.line(body.position, body.position + body.linearVelocity.cpy().scl(0.1f), Color.YELLOW)
    }

    private val polygonColor = Color(0.5f, 0.5f, 0.5f, 0.5f)

    private fun renderPolygon(entity: Entity) {
        val polygonComponent = PolygonComponent.get(entity)
        shapeDrawer.setColor(polygonColor)
        shapeDrawer.filledPolygon(polygonComponent.polygon)
        shapeDrawer.setColor(Color.WHITE)
        for (v in polygonComponent.polygon.transformedVectors()) {
            shapeDrawer.filledCircle(v, 0.4f)
        }
    }

    private fun renderDebugStuff(mapEntity: Entity) {
        val lDtkMap = LDtkMap.get(mapEntity)
        for (bound in lDtkMap.points[TypeOfPoint.Impassable]!!) {
            shapeDrawer.filledCircle(bound, 1f, Color.RED)
        }
    }

    private fun renderMap(entity: Entity) {
        val lDtkMap = LDtkMap.get(entity)
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
