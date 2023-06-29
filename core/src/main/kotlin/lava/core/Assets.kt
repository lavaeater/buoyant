package lava.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import ktx.assets.DisposableContainer
import ktx.assets.DisposableRegistry
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import twodee.ecs.ashley.components.BodyPart
import twodee.injection.InjectionContext.Companion.inject

fun assets(): Assets {
    return inject()
}

sealed class Sfx(val path: String, val duration: Float, val volume: Float = 1f) {
    object Breath : Sfx("sfx/breath.wav", 6f)
    object Bubbles : Sfx("sfx/bubbles.wav", 1f)
    object Diaphragm : Sfx("sfx/diaphragm.wav", 10f)
    object Drown: Sfx("sfx/drown.wav", 9f)
    object Intro: Sfx("sfx/intro-2-echo.wav", 18f)
    object Outro: Sfx("sfx/outro-echo.wav", 12f)
    object Ambient: Sfx("sfx/ambient-1.wav", 27f, 0.5f)
    object Creak: Sfx("sfx/creak-1.wav", 12f)
}

class Assets : DisposableRegistry by DisposableContainer() {

    private val breath = Gdx.audio.newSound("sfx/breath.wav".toInternalFile())
    private val bubbles = Gdx.audio.newSound("sfx/bubbles.wav".toInternalFile())
    private val diaphragm = Gdx.audio.newSound("sfx/diaphragm.wav".toInternalFile())
    private val drown = Gdx.audio.newSound("sfx/drown.wav".toInternalFile())
    private val intro = Gdx.audio.newSound("sfx/intro-2-echo.wav".toInternalFile())
    private val outro = Gdx.audio.newSound("sfx/outro-echo.wav".toInternalFile())

    private val mapTwoTexture = Texture("ldtk/shafts/simplified/Level_0/Tiles.png".toInternalFile())

    //        .apply { setFilter(
//        Texture.TextureFilter.Linear,
//        Texture.TextureFilter.Linear
//    ) }
    private val mapTwoIntLayer = "ldtk/shafts/simplified/Level_0/IntGrid.csv".toInternalFile().readString()

    private val head = Sprite(Texture("sprites/head.png".toInternalFile()))
    private val body = Sprite(Texture("sprites/body.png".toInternalFile()))
    private val leg = Sprite(Texture("sprites/leg.png".toInternalFile()))
    val arm = Sprite(Texture("sprites/arms.png".toInternalFile()))

    val ambiance = mapOf(Sfx.Ambient to 26f, Sfx.Creak to 11f)


    val bodyParts by lazy {
        mapOf(
            BodyPart.Head to head,
            BodyPart.Body to body,
            BodyPart.Legs to leg
        )
    }

    val maps = mapOf("two" to Pair(mapTwoTexture, mapTwoIntLayer))
    override fun dispose() {
        registeredDisposables.disposeSafely()
    }
}
