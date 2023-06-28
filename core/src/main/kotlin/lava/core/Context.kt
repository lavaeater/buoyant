package lava.core

import box2dLight.RayHandler
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.box2d.createWorld
import ktx.math.vec2
import ktx.scene2d.Scene2DSkin
import lava.SfxPlayer
import lava.ecs.systems.*
import lava.music.MusicPlayer
import lava.screens.GameScreen
import lava.ui.ToolHud
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
        Scene2DSkin.defaultSkin = Skin("ui/uiskin.json".toInternalFile())
        buildContext {
            val gameSettings = GameSettings()
            bindSingleton(gameSettings)
            bindSingleton(game)
            bindSingleton(PolygonSpriteBatch())
            bindSingleton(OrthographicCamera().apply {
                zoom = 0.12f
            })
            bindSingleton(
                ExtendViewport(
                    gameSettings.GameWidth,
                    gameSettings.GameHeight,
                    inject<OrthographicCamera>() as Camera
                )
            )
            bindSingleton(createWorld(vec2(0f, -10f)).apply {
                setContactListener(CollisionManager(game))
            })
            bindSingleton(RayHandler(inject()).apply {
                setAmbientLight(.15f)
                setBlurNum(3)
            })
            bindSingleton(ShapeDrawer(inject<PolygonSpriteBatch>() as Batch, shapeDrawerRegion))
            bindSingleton(Assets())
            bindSingleton(SfxPlayer(inject()))
            bindSingleton(MusicPlayer())
            bindSingleton(getEngine(gameSettings))
            bindSingleton(EntityFactory(inject(), inject(), inject()))
            bindSingleton(InputMultiplexer().apply {
                Gdx.input.inputProcessor = this
            })
            bindSingleton(ToolHud(inject(), inject()))
            bindSingleton(
                GameScreen(
                    inject(),
                    inject(),
                    inject(),
                    inject(),
                    inject(),
                    inject(),
                    inject<ExtendViewport>(),
                    inject()
                )
            )
        }
    }

    private fun getEngine(gameSettings: GameSettings): Engine {
        return PooledEngine().apply {
            addSystem(RemoveEntitySystem())
            addSystem(MusicSystem(inject()))
            addSystem(EmitBubblesSystem(inject()))
            addSystem(BubbleLifeSystem())
            addSystem(BubbleBuoyancySystem())
            addSystem(CrazyCameraSystem(inject(), 0.1f))
//            addSystem(CameraFollowSystem(inject(), 0.1f))
            addSystem(PlayerFlashlightSystem())
            addSystem(HeadUnderWaterSystem())
            addSystem(DiveControlSystem(inject()))
            addSystem(
                BuoyantPhysicsSystem(
                    gameSettings.TimeStep,
                    gameSettings.VelIters,
                    gameSettings.PosIters,
                    inject()
                )
            )
            addSystem(BodyControlSystem())
            addSystem(KeyboardInputSystem(inject(), invertX = false, invertY = false))
            addSystem(FlashlightDirectionSystem())
            addSystem(LightPositionUpdateSystem())
            addSystem(SteerSystem())
            addSystem(AiTimePieceSystem())
            addSystem(UpdateActionsSystem())
            addSystem(RenderSystem(inject(), inject(), inject(), inject(), inject(), inject()))
            addSystem(UpdateMemorySystem())
            addSystem(DeathSystem(inject(), inject(), inject()))
            addSystem(LogSystem())
        }
    }
}

object BuoyancySet {
    val buoyancyStuff = mutableSetOf<ContactType.Buoyancy>()
}

sealed class ContactType {
    object Unknown : ContactType()
    data class Buoyancy(val waterFixture: Fixture, val buoyantFixture: Fixture) : ContactType()
    object WinArea : ContactType()
    companion object {
        fun getContactType(contact: Contact): ContactType {
            val fixtureA = contact.fixtureA
            val fixtureB = contact.fixtureB

            if ((fixtureA.isSensor && fixtureA.filterData.categoryBits == Categories.water) && fixtureB.filterData.categoryBits == Categories.bodies) {
                return Buoyancy(fixtureA, fixtureB)
            }

            if ((fixtureB.isSensor && fixtureB.filterData.categoryBits == Categories.water) && fixtureA.filterData.categoryBits == Categories.bodies) {
                return Buoyancy(fixtureB, fixtureA)
            }

            if ((fixtureA.isSensor && fixtureA.filterData.categoryBits == Categories.winArea && (fixtureB.filterData.categoryBits == Categories.bodies || fixtureB.filterData.categoryBits == Categories.head)) ||
                (fixtureB.isSensor && fixtureB.filterData.categoryBits == Categories.winArea && (fixtureA.filterData.categoryBits == Categories.bodies || fixtureA.filterData.categoryBits == Categories.head))
            )
                return WinArea


            return Unknown
        }
    }
}

class CollisionManager(private val game: BuoyantGame) : ContactListener {
    override fun beginContact(contact: Contact) {
        when (val contactType = ContactType.getContactType(contact)) {
            ContactType.Unknown -> {}
            is ContactType.Buoyancy -> BuoyancySet.buoyancyStuff.add(contactType)
            ContactType.WinArea -> {
                if (game.gameState == GameState.Playing) {
                    game.gotoGameVictory()
                }
            }
        }
    }

    override fun endContact(contact: Contact) {
        when (val contactType = ContactType.getContactType(contact)) {
            ContactType.Unknown -> {}
            is ContactType.Buoyancy -> BuoyancySet.buoyancyStuff.remove(contactType)
            ContactType.WinArea -> {}
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {
    }
}

