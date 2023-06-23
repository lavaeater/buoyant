package lava.ui

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.ashley.allOf
import ktx.scene2d.actors
import ktx.scene2d.table
import ktx.scene2d.verticalGroup
import twodee.ecs.ashley.components.Player
import twodee.ui.LavaHud

class ToolHud(batch: PolygonSpriteBatch, private val inputMultiplexer: InputMultiplexer) : LavaHud(batch) {
    /**
     * Now you need to flexbox this UI into something usable.
     *
     * The tree must be expanded at all times.
     */
    override val stage by lazy {
        Stage(hudViewPort, batch).apply {
            actors {
                table {
                    // MAIN TABLE
                    setFillParent(true)
                    table {
                        // TOP ROW
                        table {
                            // TREE TABLE
//TOP LEFT

                        }
                            .inCell
                            .left()
                            .top()
                            .fill()
                            .expand()
                        table {
                            // CENTER TOP COLUMN
                            row()
                        }
                            .inCell
                            .center()
                            .expand()
                        table {
                            // TOP RIGHT COLUMN
                            row()
                        }
                            .inCell
                            .right()
                            .expand()
                    }
                        .inCell
                        .top()
                        .height(hudViewPort.worldHeight * 0.1f)
                    row()
                    table {
                        // MIDDLE TABLE / MAIN SCREEN BASICALLY
                        verticalGroup {
                            // LEFT COLUMN
                        }
                            .inCell
                            .left()
                            .fillY()
                            .width(hudViewPort.worldWidth * 0.1f)
                        table {
                            // CENTER TABLE
                        }
                            .inCell
                            .fill()
                            .expand()
                        verticalGroup {
                            // RIGHT COLUMN
                        }
                            .inCell
                            .right()
                            .fillY()
                            .width(hudViewPort.worldWidth * 0.1f)

                    }
                        .inCell
                        .fill()
                        .expand()
                    row()
                    table {
                        // BOTTOM ROW
                    }
                        .inCell
                        .expand()
                        .fill()
                }
            }
            inputMultiplexer.addProcessor(this)
        }
    }
}
