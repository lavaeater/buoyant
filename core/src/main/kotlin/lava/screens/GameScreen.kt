package lava.screens

import box2dLight.RayHandler
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.ashley.allOf
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.log.info
import ktx.math.vec2
import ktx.math.vec3
import lava.core.BuoyantGame
import lava.core.EntityFactory
import lava.core.GameState
import lava.ecs.components.Direction
import lava.ecs.components.DiveControl
import lava.music.MusicPlayer
import lava.ui.ToolHud
import twodee.core.engine
import twodee.core.world
import twodee.injection.InjectionContext.Companion.inject
import twodee.input.CommandMap
import twodee.screens.BasicScreen

class GameScreen(
    private val game: BuoyantGame,
    private val entityFactory: EntityFactory,
    private val hud: ToolHud,
    private val musicPlayer: MusicPlayer,
    camera: OrthographicCamera,
    viewport: Viewport,
    batch: PolygonSpriteBatch
) : BasicScreen(game, camera, viewport, batch) {
    init {
        commandMap = CommandMap("Stuff").apply {
            setBoth(
                Input.Keys.W,
                "Swim up",
                {
                    diveControl.remove(Direction.Swim)
                }, {
                    diveControl.add(Direction.Swim)
                })
            setBoth(
                Input.Keys.S,
                "Swim down",
                {
                    diveControl.remove(Direction.Hover)
                }, {
                    diveControl.add(Direction.Hover)
                })

            setBoth(
                Input.Keys.A,
                "Swim left",
                {
                    diveControl.remove(Direction.RotateLeft)
                }, {
                    diveControl.add(Direction.RotateLeft)
                })
            setBoth(
                Input.Keys.D,
                "Swim right",
                {
                    diveControl.remove(Direction.RotateRight)
                }, {
                    diveControl.add(Direction.RotateRight)
                })

            setBoth(
                Input.Keys.LEFT,
                "Rotate Cam Left",
                {
                    cameraRotation = 0f
                }, {
                    cameraRotation = 1f
                })
            setBoth(
                Input.Keys.RIGHT,
                "Rotate Cam Right",
                {
                    cameraRotation = 0f
                }, {
                    cameraRotation = -1f
                })
            setBoth(
                Input.Keys.UP,
                "Zoom Camera In",
                {
                    cameraZoom = 0f
                    info { "cameraZoom: ${camera.zoom}" }
                }, {
                    cameraZoom = -1f
                })
            setBoth(
                Input.Keys.DOWN,
                "Zoom Camera Out",
                {
                    cameraZoom = 0f
                    info { "cameraZoom: ${camera.zoom}" }
                }, {
                    cameraZoom = 1f
                })
            setDown(
                Input.Keys.M,
                "Toggle Music") {
                musicPlayer.toggle()
                info { "cameraZoom: ${camera.zoom}" }
            }
        }
    }

    private val dummyControl = DiveControl()
    private val diveControlFamily = allOf(DiveControl::class).get()
    private val divers get() = engine().getEntitiesFor(diveControlFamily)
    private val diveControl: DiveControl
        get() {
            return if (divers.any()) {
                DiveControl.get(divers.first())
            } else {
                dummyControl
            }
        }

    private val cameraDirection = vec2()

    private var cameraRotation = 0f
    private var cameraZoom = 0f

    private val image = Texture("logo.png".toInternalFile(), true).apply {
        setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        )
    }

    override fun render(delta: Float) {
//        updateCamera(delta)
        engine().update(delta)
        hud.render(delta)
    }

    override fun renderBatch(delta: Float) {
        //NO OP FOR THIS CLASS THIS TIME
    }

    private fun updateCamera(delta: Float) {
        if (cameraRotation != 0f) {
            camera.rotate(cameraRotation * delta * 5f)
        }
        if (cameraZoom != 0f) {
            camera.zoom += cameraZoom * delta * 5f
        }
        if (cameraDirection != Vector2.Zero) {
            val targetPosition = camera.position.cpy().add(vec3(cameraDirection.cpy().scl(delta * 250f), 0f))
            camera.position.lerp(targetPosition, 0.2f)
        }
    }

    override fun show() {
        super.show()
        if (game.gameState == GameState.GameStart) {
            entityFactory.createMap("two")
            engine().systems.forEach { it.setProcessing(true) }
            game.gameState = GameState.Playing
        }
    }

    override fun hide() {
        super.hide()
        if (game.gameState == GameState.GameOver || game.gameState == GameState.GameVictory) {
            engine().systems.forEach { it.setProcessing(false) }
            engine().removeAllEntities()
            val bodyArray = gdxArrayOf<Body>(false, world().bodyCount)
            world().getBodies(bodyArray)
            bodyArray.forEach { body ->
                world().destroyBody(body)
            }
            inject<RayHandler>().removeAll()
        }
    }

    override fun dispose() {
        image.disposeSafely()
        batch.disposeSafely()
    }
}
