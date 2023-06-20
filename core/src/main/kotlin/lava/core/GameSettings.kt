package lava.core


class GameSettings {
    val Debug = true
    val GameWidth = 72f
    val AspectRatio = 16f / 9f
    val GameHeight = AspectRatio * GameWidth
    val PixelsPerMeter = 8f
    val MetersPerPixel = 1f / PixelsPerMeter
    val outerShellHz = 25f
    val outerShellDamp = 0.5f
    val spokeHz = 1f
    val spokeDamp = 0.3f
    val segmentLength = 5f
    val TimeStep = 1 / 60f
    val VelIters = 16
    val PosIters = 6
}
