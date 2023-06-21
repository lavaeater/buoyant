package lava.screens

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import ktx.ashley.allOf
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.log.info
import ktx.math.vec2
import ktx.math.vec3
import lava.core.BuoyantGame
import lava.core.EntityFactory
import lava.core.GameSettings
import lava.ecs.components.Direction
import lava.ecs.components.DiveControl
import twodee.core.engine
import twodee.input.CommandMap
import twodee.screens.BasicScreen

class GameScreen(
    game: BuoyantGame,
    private val gameSettings: GameSettings,
    private val entityFactory: EntityFactory
) : BasicScreen(game) {
    init {
        commandMap = CommandMap("Stuff").apply {
            setBoth(
                Input.Keys.W,
                "Swim up",
                {
                    diveControl.add(Direction.Up)
                }, {
                    diveControl.remove(Direction.Up)
                })
            setBoth(
                Input.Keys.S,
                "Swim down",
                {
                    diveControl.add(Direction.Down)
                }, {
                    diveControl.remove(Direction.Down)
                })

            setBoth(
                Input.Keys.A,
                "Swim left",
                {
                    diveControl.add(Direction.Left)
                }, {
                    diveControl.remove(Direction.Left)
                })
            setBoth(
                Input.Keys.D,
                "Swim right",
                {
                    diveControl.add(Direction.Right)
                }, {
                    diveControl.remove(Direction.Right)
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

    private var needsLevel = true

    override fun render(delta: Float) {
        updateCamera(delta)

        engine().update(delta)
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
        if (needsLevel)
            entityFactory.createMap("two")
        needsLevel = false
    }

    override fun dispose() {
        image.disposeSafely()
        batch.disposeSafely()
    }
}
