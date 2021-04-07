package fy.game.blockpuzzle.gamedefinition

import fy.game.blockpuzzle.block.BlockTypes
import fy.game.blockpuzzle.game.GameEngineBuilder
import fy.game.blockpuzzle.playingfield.PlayingField

/**
 * Fills String representation into playing field
 */
class StartPlayingFieldFiller {

    fun fillStartPlayingField(playingField: PlayingField, stringRepresentation: String) {
        var def = stringRepresentation
        val blockTypes = BlockTypes(null)
        while (def.startsWith("\n")) {
            def = def.substring(1)
        }
        val lines = def.split("\n")
        val blocks = GameEngineBuilder.blocks
        for (y in 0 until blocks) {
            for (x in 0 until blocks) {
                val c = lines[y][x]
                if (c != '_') {
                    val blockType = blockTypes.getBlockTypeNumber(c)
                    playingField.set(x, y, blockType)
                }
            }
        }
        playingField.draw()
    }
}