package lava.core

import ktx.async.KtxAsync
import lava.screens.GameOverScreen
import lava.screens.GameScreen
import lava.screens.SplashScreen
import twodee.core.MainGame
import twodee.injection.InjectionContext.Companion.inject

sealed class GameState {
    object Splash : GameState()
    object GameStart: GameState()
    object Playing : GameState()
    object GameOver : GameState()
    object GameVictory : GameState()
}

class BuoyantGame : MainGame() {

    var gameState:GameState = GameState.Splash

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

    override fun gotoGameVictory() {
        gameState = GameState.GameVictory
        setScreen<GameOverScreen>()
    }

    override fun create() {
        KtxAsync.initiate()
        Context.initialize(this)

        addScreen(inject<GameScreen>())
        addScreen(GameOverScreen(this))
        addScreen(SplashScreen(this))
        setScreen<SplashScreen>()
    }
}

