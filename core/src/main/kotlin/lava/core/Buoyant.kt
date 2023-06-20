package lava.core

import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync

class Buoyant : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        addScreen(GameScreen())
        setScreen<GameScreen>()
    }
}

