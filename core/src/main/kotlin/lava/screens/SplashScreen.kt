package lava.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.actors.stage
import ktx.assets.toInternalFile
import ktx.graphics.use
import ktx.scene2d.actors
import ktx.scene2d.label
import ktx.scene2d.table
import lava.core.BuoyantGame
import lava.core.GameSettings
import twodee.input.CommandMap
import twodee.screens.BasicScreen
import twodee.screens.ScreenWithStage

class SplashScreen(
    game: BuoyantGame,
    viewport: Viewport,
    batch: PolygonSpriteBatch
) :
    ScreenWithStage(
        game,
        viewport,
        viewport.camera as OrthographicCamera,
        batch
    ) {
    init {
        commandMap = CommandMap("Splash").apply {
            setUp(
                Input.Keys.SPACE,
                "Start Game"
            ) {
                game.goToGameScreen()
            }
        }
    }

    val bgTexture by lazy { Texture("bg-textures/splash.jpg".toInternalFile()) }
    override val stage: Stage by lazy { stage(batch, viewport).apply {
                actors {
                    table {
                        // MAIN TABLE
                        setFillParent(true)
                        label("PRESS SPACE TO BEGIN")
                            .inCell
                            .grow()
                            .fill()
                    }
                }
            }
        }

    override fun renderBatch(delta: Float) {
        batch.draw(
            bgTexture,
            0f,
            0f, viewport.worldWidth, viewport.worldHeight
        )
    }

}
