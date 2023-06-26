package lava.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.assets.toInternalFile
import ktx.graphics.use
import lava.core.BuoyantGame
import lava.core.GameSettings
import lava.core.GameState
import twodee.injection.InjectionContext
import twodee.input.CommandMap
import twodee.screens.BasicScreen

class GameOverScreen(
    private val game: BuoyantGame,
    camera: OrthographicCamera,
    viewport: Viewport,
    batch: PolygonSpriteBatch
) : BasicScreen(game, camera, viewport, batch) {
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

    override fun render(delta: Float) {
        clearScreenUpdateCamera(delta)
        batch.use {
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
}
