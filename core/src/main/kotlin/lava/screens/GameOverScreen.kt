package lava.screens

import com.badlogic.gdx.Input
import lava.core.BuoyantGame
import twodee.input.CommandMap
import twodee.screens.BasicScreen

class GameOverScreen(game: BuoyantGame) : BasicScreen(game) {
    init {
        commandMap = CommandMap("GameOver").apply {
            setUp(
                Input.Keys.SPACE,
                "Go To Splash Screen") {
                game.goToGameSelect()
            }
        }
    }
}
