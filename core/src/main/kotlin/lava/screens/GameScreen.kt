package lava.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.graphics.use
import lava.core.BuoyantGame
import lava.core.EntityFactory
import lava.core.GameSettings
import twodee.core.engine
import twodee.screens.BasicScreen

class GameScreen(
    game: BuoyantGame,
    private val gameSettings: GameSettings,
    private val entityFactory: EntityFactory
) : BasicScreen(game, Color.BLACK), KtxScreen, KtxInputAdapter {
    private val image = Texture("logo.png".toInternalFile(), true).apply { setFilter(
        Texture.TextureFilter.Linear,
        Texture.TextureFilter.Linear
    ) }

    override fun render(delta: Float) {
        super<BasicScreen>.render(delta)
        engine().update(delta)
    }

    override fun show() {
        super<BasicScreen>.show()
    }

    override fun dispose() {
        image.disposeSafely()
        batch.disposeSafely()
    }
}
