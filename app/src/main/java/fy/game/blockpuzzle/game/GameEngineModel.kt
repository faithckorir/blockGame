package fy.game.blockpuzzle.game

import fy.game.blockpuzzle.block.BlockTypes
import fy.game.blockpuzzle.game.place.IPlaceAction
import fy.game.blockpuzzle.gamedefinition.OldGameDefinition
import fy.game.blockpuzzle.gamepiece.Holders
import fy.game.blockpuzzle.gamepiece.INextGamePiece
import fy.game.blockpuzzle.gamestate.GameState
import fy.game.blockpuzzle.playingfield.PlayingField
import fy.game.blockpuzzle.playingfield.gravitation.GravitationData

/**
 * Game engine model
 */
data class GameEngineModel(
    // Immutable properties only!
    val blocks: Int,
    val blockTypes: BlockTypes,
    val view: IGameView,
    val gs: GameState,
    val definition: OldGameDefinition,
    val playingField: PlayingField,
    val holders: Holders,
    val placeActions: List<IPlaceAction>,
    val gravitation: GravitationData,
    val nextGamePiece: INextGamePiece
) {

    fun save() {
        val ss = gs.get()
        playingField.save(ss)
        gravitation.save(ss)
        holders.save(ss)
        gs.save()
    }
}
