package lava.screens

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.actors.stage
import ktx.assets.toInternalFile
import ktx.scene2d.actors
import ktx.scene2d.label
import ktx.scene2d.table
import lava.core.BuoyantGame
import lava.core.GameState
import twodee.extensions.boundLabel
import twodee.input.CommandMap
import twodee.screens.ScreenWithStage

class GameOverScreen(
    private val game: BuoyantGame,
    viewport: Viewport,
    batch: PolygonSpriteBatch
) : ScreenWithStage(game, viewport, viewport.camera as OrthographicCamera, batch) {
    init {
        commandMap = CommandMap("GameOver").apply {
            setUp(
                Input.Keys.SPACE,
                "Go To Splash Screen"
            ) {
                game.goToGameSelect()
            }
        }
    }

    private val winTexture by lazy { Texture("bg-textures/victory.jpg".toInternalFile()) }
    private val deathTexture by lazy { Texture("bg-textures/death.jpg".toInternalFile()) }
    override val stage: Stage by lazy {
        stage(batch, viewport).apply {
            actors {
                table {
                    // MAIN TABLE
                    setFillParent(true)
                    boundLabel({ if(game.gameState == GameState.GameVictory) "You made it out." else "YOU DROWNED" })
                        .inCell
                        .grow()
                        .fill()
                }
            }
        }
    }

    override fun renderBatch(delta: Float) {
        if (game.gameState == GameState.GameVictory) {
            batch.draw(
                winTexture,
                0f, 0f,
                viewport.worldWidth,
                viewport.worldHeight
            )
        }
        if (game.gameState == GameState.GameOver) {
            batch.draw(
                deathTexture,
                0f, 0f,
                viewport.worldWidth,
                viewport.worldHeight
            )
        }
    }
}
