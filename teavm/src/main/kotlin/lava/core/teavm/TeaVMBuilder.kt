package lava.core.teavm

import java.io.File
import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder
import com.github.xpenatan.gdx.backends.teavm.plugins.TeaReflectionSupplier
import com.github.xpenatan.gdx.backends.teavm.gen.SkipClass

/** Builds the TeaVM/HTML application. */
@SkipClass
object TeaVMBuilder {
    @JvmStatic fun main(arguments: Array<String>) {
        val teaBuildConfiguration = TeaBuildConfiguration().apply {
            assetsPath.add(File("../assets"))
            webappPath = File("build/dist").canonicalPath
            // Register any extra classpath assets here:
            // additionalAssetsClasspathFiles += "lava/core/asset.extension"
        }

        // Register any classes or packages that require reflection here:
        // TeaReflectionSupplier.addReflectionClass("lava.core.reflect")

        val tool = TeaBuilder.config(teaBuildConfiguration)
        tool.mainClass = "lava.core.teavm.TeaVMLauncher"
        TeaBuilder.build(tool)
    }
}
