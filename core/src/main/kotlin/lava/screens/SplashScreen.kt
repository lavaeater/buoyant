package lava.screens

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
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

    val bgTexture = Texture()

}
