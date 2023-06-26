package lava.core

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
