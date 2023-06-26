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
        gameState = GameState.GameOver
        setScreen<GameOverScreen>()
    }

    /**
     * We should give it a minute. Start a timer, will ya?
     */
    override fun gotoGameVictory() {
        Timer.schedule(object: Task() {
            override fun run() {
                gameState = GameState.GameVictory
                setScreen<GameOverScreen>()
            }

        }, 5f)
        Timer.instance().start()
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
                    gameSettings.GameWidth,
                    gameSettings.GameHeight,
                    OrthographicCamera()
                ), inject()
            )
        )
        addScreen(
            SplashScreen(
                this,
                ExtendViewport(
                    gameSettings.GameWidth,
                    gameSettings.GameHeight,
                    OrthographicCamera()
                ),
                inject()
            )
        )

        setScreen<SplashScreen>()
    }
}

