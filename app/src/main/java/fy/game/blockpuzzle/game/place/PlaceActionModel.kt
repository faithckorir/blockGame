package fy.game.blockpuzzle.game.place

import fy.game.blockpuzzle.game.DropActionModel
import fy.game.blockpuzzle.game.GameEngineInterface
import fy.game.blockpuzzle.game.GameEngineModel
import fy.game.blockpuzzle.playingfield.FilledRows

/**
 * All data needed by an IPlaceAction
 */
data class PlaceActionModel(
    // static data:
    val gameEngineInterface: GameEngineInterface,
    private val model: GameEngineModel, // view is private
    // dynamic data:
    private val dropActionModel: DropActionModel,
    private val filledRows: FilledRows // must be a val, must not be calculated here
) {
    fun getBlocks() = model.blocks
    fun getBlockTypes() = model.blockTypes
    fun getMessages() = model.view.getMessages()
    fun playMoreThan40PercentSound() = model.view.playSound(1)
    fun playEmptyScreenBonusSound() = model.view.playSound(2)
    fun getGs() = model.gs
    fun getDefinition() = model.definition
    fun getPlayingField() = model.playingField
    fun getGravitation() = model.gravitation
    fun getFilledRows(): FilledRows = filledRows
    fun getGamePiece() = dropActionModel.gamePiece
    fun getPosition() = dropActionModel.xy
}
