package lava.screens

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import lava.core.BuoyantGame
import lava.core.EntityFactory
import lava.core.GameSettings
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
                Input.Keys.A,
                "Rotate Cam Left",
                {
                cameraRotation = 0f
                }, {
                    cameraRotation = 1f
                })
            setBoth(
                Input.Keys.D,
                "Rotate Cam Right",
                {
                cameraRotation = 0f
                }, {
                    cameraRotation = -1f
                })
        }
    }

    var cameraRotation = 0f

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
        if(cameraRotation != 0f) {
            camera.rotate(cameraRotation * delta * 5f)
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
