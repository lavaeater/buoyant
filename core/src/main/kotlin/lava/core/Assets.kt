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

sealed class Sfx {
    object Breath : Sfx()
    object Bubbles : Sfx()
    object Diaphragm : Sfx()
    object Drown: Sfx()
    object Intro: Sfx()
    object Outro: Sfx()
}

class Assets : DisposableRegistry by DisposableContainer() {

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

    private val breath = Gdx.audio.newSound("sfx/breath.wav".toInternalFile())
    private val bubbles = Gdx.audio.newSound("sfx/bubbles.wav".toInternalFile())
    private val diaphragm = Gdx.audio.newSound("sfx/diaphragm.wav".toInternalFile())
    private val drown = Gdx.audio.newSound("sfx/drown.wav".toInternalFile())
    private val intro = Gdx.audio.newSound("sfx/intro-2-echo.wav".toInternalFile())
    private val outro = Gdx.audio.newSound("sfx/outro-echo.wav".toInternalFile())

    val sfx by lazy {
        mapOf(
            Sfx.Breath to breath,
            Sfx.Bubbles to bubbles,
            Sfx.Diaphragm to diaphragm,
            Sfx.Drown to drown,
            Sfx.Intro to intro,
            Sfx.Outro to outro
        )
    }

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
