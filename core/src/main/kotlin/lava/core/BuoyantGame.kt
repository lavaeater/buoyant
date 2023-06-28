package lava.core

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.Timer.Task
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.async.KtxAsync
import lava.screens.GameOverScreen
import lava.screens.GameScreen
import lava.screens.SplashScreen
import twodee.core.MainGame
import twodee.injection.InjectionContext.Companion.inject

sealed class GameState {
    object Splash : GameState()
    object GameStart : GameState()
    object Playing : GameState()
    object GameOver : GameState()
    object GameVictory : GameState()
    object WaitForVictory : GameState()
    object WaitForGameOver : GameState()
}

class BuoyantGame : MainGame() {

    var gameState: GameState = GameState.Splash

    override fun goToGameSelect() {
        gameState = GameState.Splash
        setScreen<SplashScreen>()
    }

    override fun goToGameScreen() {
        gameState = GameState.GameStart
        setScreen<GameScreen>()
    }

    override fun goToGameOver() {
        if( gameState == GameState.Playing) {
            gameState = GameState.WaitForGameOver
            Timer.schedule(object : Task() {
                override fun run() {
                    gameState = GameState.GameOver
                    setScreen<GameOverScreen>()
                }

            }, 2.5f)
        }
    }

    /**
     * We should give it a minute. Start a timer, will ya?
     */
    override fun gotoGameVictory() {
        if( gameState == GameState.Playing) {
            gameState = GameState.WaitForVictory
            Timer.schedule(object : Task() {
                override fun run() {
                    gameState = GameState.GameVictory
                    setScreen<GameOverScreen>()
                }

            }, 2.5f)
        }
    }

    override fun create() {
        KtxAsync.initiate()
        Context.initialize(this)

        addScreen(inject<GameScreen>())
        val gameSettings = inject<GameSettings>()
        addScreen(
            GameOverScreen(
                this,
                ExtendViewport(
                    gameSettings.GameWidth * 2,
                    gameSettings.GameHeight * 2,
                    OrthographicCamera()
                ), inject()
            )
        )
        addScreen(
            SplashScreen(
                this,
                ExtendViewport(
                    gameSettings.GameWidth * 2,
                    gameSettings.GameHeight * 2,
                    OrthographicCamera()
                ),
                inject()
            )
        )

        setScreen<SplashScreen>()
    }
}

