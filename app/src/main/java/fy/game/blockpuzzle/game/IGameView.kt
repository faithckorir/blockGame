package fy.game.blockpuzzle.game

import fy.game.blockpuzzle.gamepiece.IGamePieceView
import fy.game.blockpuzzle.global.messages.MessageFactory
import fy.game.blockpuzzle.playingfield.Action
import fy.game.blockpuzzle.playingfield.IPlayingFieldView

/**
 * Alle Zugriffe vom Game (Controller) auf die View Schicht
 */
interface IGameView {

    // init phase
    fun getPlayingFieldView(): IPlayingFieldView

    // init phase (and internal use)
    fun getGamePieceView(index: Int): IGamePieceView

    fun showScore(text: String)

    fun showMoves(text: String)

    fun showTerritoryName(resId: Int)

    fun shake()

    fun playSound(number: Int)

    fun getSpecialAction(specialState: Int): Action

    fun getMessages(): MessageFactory
}