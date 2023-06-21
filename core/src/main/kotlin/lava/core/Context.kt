package lava.core

import box2dLight.RayHandler
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.math.vec2
import lava.ecs.systems.BuoyancySystem
import lava.ecs.systems.RenderSystem
import lava.screens.GameScreen
import space.earlygrey.shapedrawer.ShapeDrawer
import twodee.ecs.ashley.systems.*
import twodee.injection.InjectionContext

object Context : InjectionContext() {
    private val shapeDrawerRegion: TextureRegion by lazy {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.drawPixel(0, 0)
        val texture = Texture(pixmap) //remember to dispose of later
        pixmap.disposeSafely()
        TextureRegion(texture, 0, 0, 1, 1)
    }

    fun initialize(game: BuoyantGame) {
        buildContext {
            val gameSettings = GameSettings()
            bindSingleton(gameSettings)
            bindSingleton(game)
            bindSingleton(PolygonSpriteBatch())
            bindSingleton(OrthographicCamera().apply {
                zoom = 0.16f
            })
            bindSingleton(
                ExtendViewport(
                    gameSettings.GameWidth,
                    gameSettings.GameHeight,
                    inject<OrthographicCamera>() as Camera
                )
            )
            bindSingleton(createWorld(vec2(0f, -10f)).apply {
                setContactListener(CollisionManager())
            })
            bindSingleton(RayHandler(inject()).apply {
                setAmbientLight(.01f)
                setBlurNum(3)
            })
            bindSingleton(ShapeDrawer(inject<PolygonSpriteBatch>() as Batch, shapeDrawerRegion))
            bindSingleton(getEngine(gameSettings))
            bindSingleton(Assets())
            bindSingleton(EntityFactory(inject(), inject(), inject()))
            //bindSingleton(HackLightEngine(0.01f, 0.01f, 0.01f, 0.1f))
            bindSingleton(
                GameScreen(
                    inject(),
                    inject(),
                    inject()
                )
            )
        }
    }

    private fun getEngine(gameSettings: GameSettings): Engine {
        return PooledEngine().apply {
            addSystem(RemoveEntitySystem())
//            addSystem(CameraAndMapSystem(inject(), 0.75f, inject(), inject<GameSettings>().AspectRatio))
            addSystem(CameraFollowSystem(inject(), 0.5f))
            addSystem(BuoyancySystem())
            addSystem(Box2dUpdateSystem(gameSettings.TimeStep, gameSettings.VelIters, gameSettings.PosIters))
            addSystem(BodyControlSystem())
            addSystem(KeyboardInputSystem(inject(), invertX = false, invertY = false))
            addSystem(FlashlightDirectionSystem())
            addSystem(LightPositionUpdateSystem())
            addSystem(SteerSystem())
            addSystem(AiTimePieceSystem())
            addSystem(UpdateActionsSystem())
            addSystem(RenderSystem(inject(), inject(), inject(), inject(), inject()))
            addSystem(Box2dDebugRenderSystem(inject(), inject()))
            addSystem(UpdateMemorySystem())
            addSystem(LogSystem())
        }
    }
}

sealed class ContactType {
    object Unknown : ContactType()

    companion object {
        fun getContactType(contact: Contact): ContactType {
            return Unknown
        }
    }
}

class CollisionManager : ContactListener {
    override fun beginContact(contact: Contact) {
        when (val contactType = ContactType.getContactType(contact)) {
            ContactType.Unknown -> {}
        }
    }

    override fun endContact(contact: Contact) {
        when (val contactType = ContactType.getContactType(contact)) {

            ContactType.Unknown -> {}
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {
    }
}

