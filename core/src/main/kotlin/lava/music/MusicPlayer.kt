package lava.music

import com.badlogic.gdx.ai.GdxAI
import twodee.music.*

class MusicPlayer {
    private val sampleBaseDir = "instruments"

    private val kickSampler by lazy { loadSampler("Kick", "drums-1.txt", sampleBaseDir) }
    private val snareSampler by lazy { loadSampler("Snare", "drums-1.txt", sampleBaseDir) }
    private val hatSampler by lazy { loadSampler("ClHat", "drums-1.txt", sampleBaseDir) }
    private val bassSampler by lazy { loadSampler("lofi-bass", "lo-fi-1.txt", sampleBaseDir) }
    private val rythmGuitarSampler by lazy { loadSampler("rythm-guitar-c", "guitar-1.txt", sampleBaseDir) }
    private val soloSampler by lazy { loadSampler("lead-c", "lo-fi-1.txt", sampleBaseDir) }
    private val leadSampler by lazy { loadSampler("fxpad", "lo-fi-1.txt", sampleBaseDir) }
    private val kickBeat = floatArrayOf(
        1f, -1f, -1f, 0.1f,
        0.4f, -1f, 0.4f, -1f,
        0.9f, -1f, -1f, 0.2f,
        0.5f, -1f, 0.3f, -1f
    ).mapIndexed { i, s -> i to Note(0, s) }.toMap().toMutableMap()

//    private val superBassBeat = floatArrayOf(
//        1f, 0f, 0f, 0.1f,
//        0.4f, 0f, 0.4f, 0f,
//        0.9f, 0f, 0f, 0.2f,
//        0.5f, 0f, 0.3f, 0f
//    ).mapIndexed { i, s -> i to Note(0, s) }.toMap().toMutableMap()

    private val snareBeat = floatArrayOf(
        -1f, 0.1f, -1f, 0.2f,
        1f, -1f, 0.5f, -1f,
        -1f, 0.2f, -1f, 0.2f,
        1f, -1f, 0.25f, 0.1f
    ).mapIndexed { i, s -> i to Note(0, s) }.toMap().toMutableMap()

    private val hatBeat = floatArrayOf(
        1f, 0.1f, 0.9f, 0.3f,
        1f, 0.5f, 0.7f, 0.4f,
        1f, 0.2f, 0.8f, 0.3f,
        1f, 0.4f, 0.7f, 0.1f
    ).mapIndexed { i, s -> i to Note(0, s) }.toMap().toMutableMap()

    private val scaleNotes = listOf(
        Note(-1, 1f),
        Note(1, 0.2f),
        Note(3, 0.6f),
        Note(4, 0.5f),
        Note(6, 0.7f),
        Note(8, 0.4f),
        Note(9, 0f),
    )

    private val signalConductor =
        SignalConductor(
            80f,
            4f,
            4f,
            mutableListOf(
                SignalDrummer("kick", kickSampler, kickBeat),
                SignalDrummer("snare", snareSampler, snareBeat),
                SignalDrummer("hat", hatSampler, hatBeat),
                SignalBass("bass", bassSampler),
                SoloMusician("soolooo", listOf(leadSampler)),
                SoloMusician("soolooo", listOf(soloSampler, rythmGuitarSampler)),
            ),
            generateChords()
        )

    private fun generateChords(): MutableList<Chord> {
        return mutableListOf(
            Chord(
                0f,
                listOf(
                    Note(-1, 1f),
                    Note(3, 0.25f),
                    Note(7, 0.5f),
                    Note(11, 0f),
                ), scaleNotes

            ),
            Chord(
                1f,
                listOf(
                    Note(-3, 1f),
                    Note(1, 0.25f),
                    Note(4, 0.5f),
                    Note(7, 0f),
                ), scaleNotes
            ),
            Chord(
                2f,
                listOf(
                    Note(-5, 1f),
                    Note(-1, 0.25f),
                    Note(2, 0.5f),
                    Note(5, 0f),
                ), scaleNotes
            ),
            Chord(
                3f,
                listOf(
                    Note(0, 1f),
                    Note(2, 0.25f),
                    Note(4, 0.5f),
                    Note(6, 0f),
                ), scaleNotes
            ),
        )
    }

    private val timePiece by lazy { GdxAI.getTimepiece() }

    fun update() {
        signalConductor.update()
        playSounds()
    }

    fun play() {
        signalConductor.play()
    }

    fun stop() {
        signalConductor.stop()
    }

    var intensity: Float get() = signalConductor.baseIntensity
        set(value) {
            signalConductor.baseIntensity = value
        }

    private fun playSounds() {
        val soundsToPlayRightNowIGuess = ToPlay.soundsToPlay.filter { it.targetTime < timePiece.time }
        ToPlay.soundsToPlay.removeAll(soundsToPlayRightNowIGuess)
        for (sound in soundsToPlayRightNowIGuess) {
            sound.soundSource.play(1f, sound.pitch, 0f)
        }
    }
}
