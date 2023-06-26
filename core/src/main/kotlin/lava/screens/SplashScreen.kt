package lava.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.assets.toInternalFile
import ktx.graphics.use
import lava.core.BuoyantGame
import lava.core.GameSettings
import twodee.input.CommandMap
import twodee.screens.BasicScreen

class SplashScreen(
    game: BuoyantGame,
    camera: OrthographicCamera,
    viewport: Viewport,
    batch: PolygonSpriteBatch
) :
    BasicScreen(
        game,
        camera, viewport, batch
    ) {
    init {
        commandMap = CommandMap("Splash").apply {
            setUp(
                Input.Keys.SPACE,
                "Start Game"
            ) {
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
