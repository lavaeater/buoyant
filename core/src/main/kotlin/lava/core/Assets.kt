package lava.core

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.assets.DisposableContainer
import ktx.assets.DisposableRegistry
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import twodee.injection.InjectionContext.Companion.inject

fun assets(): Assets {
    return inject()
}

class Assets : DisposableRegistry by DisposableContainer() {

    private val mapTwo = Texture("maps/new_level/simplified/Level_0/_composite.png".toInternalFile())
    private val mapTwoTop = Texture("maps/new_level/simplified/Level_0/Walls.png".toInternalFile())
    private val mapTwoIntLayer = "maps/new_level/simplified/Level_0/IntGrid.csv".toInternalFile().readString()

    val maps = mapOf("two" to Triple(mapTwo, mapTwoIntLayer, mapTwoTop))
    override fun dispose() {
        registeredDisposables.disposeSafely()
    }
}
