package lava.core

import com.badlogic.gdx.graphics.Texture
import ktx.assets.DisposableContainer
import ktx.assets.DisposableRegistry
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
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

    val maps = mapOf("two" to Pair(mapTwoTexture, mapTwoIntLayer))
    override fun dispose() {
        registeredDisposables.disposeSafely()
    }
}
