package lava.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import ktx.assets.toInternalFile
import ktx.graphics.use
import lava.core.BuoyantGame
import twodee.input.CommandMap
import twodee.screens.BasicScreen

class GameOverScreen(private val game: BuoyantGame) : BasicScreen(game) {
    init {
        commandMap = CommandMap("GameOver").apply {
            setUp(
                Input.Keys.SPACE,
                "Go To Splash Screen") {
                game.goToGameSelect()
            }
        }
    }
    private val winTexture = Texture("bg-textures/victory.jpg".toInternalFile())
    private val deathTexture = Texture("bg-textures/death.jpg".toInternalFile())

    override fun render(delta: Float) {
        batch.use {
            batch.draw(
                if(game.gameState is lava.core.GameState.GameVictory) winTexture else deathTexture,
                0f, 0f,
                Gdx.graphics.width.toFloat(),
                Gdx.graphics.height.toFloat()
            )
        }
    }
}
