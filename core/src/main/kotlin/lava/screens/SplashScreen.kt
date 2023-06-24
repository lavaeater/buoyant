package lava.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import ktx.assets.toInternalFile
import ktx.graphics.use
import lava.core.BuoyantGame
import twodee.input.CommandMap
import twodee.screens.BasicScreen

class SplashScreen(game: BuoyantGame) : BasicScreen(game) {
    init {
        commandMap = CommandMap("Splash").apply {
            setUp(
                Input.Keys.SPACE,
                "Start Game") {
                game.goToGameScreen()
            }
        }
    }

    val bgTexture by lazy { Texture("bg-textures/splash.jpg".toInternalFile()) }

    override fun render(delta: Float) {
        batch.use {
            batch.draw(bgTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        }
    }

}
