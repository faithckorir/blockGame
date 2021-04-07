package fy.game.blockpuzzle.gamedefinition;

import fy.game.blockpuzzle.gamepiece.INextGamePiece;
import fy.game.blockpuzzle.gamepiece.RandomGamePiece;
import fy.game.blockpuzzle.gamestate.GameState;
import fy.game.blockpuzzle.gamestate.Spielstand;

/**
 * Game definition for old game
 */
public class OldGameDefinition {

    public int getGravitationStartRow() {
        return 5;
    }

    public int getGamePieceBlocksScoreFactor() {
        return 1;
    }

    public int getHitsScoreFactor() {
        return 10;
    }

    public boolean isRowsAdditionalBonusEnabled() {
        return true;
    }

    public boolean gameGoesOnAfterWonGame() {
        return true;
    }

    public INextGamePiece createNextGamePieceGenerator(GameState gs) {
        return new RandomGamePiece();
    }

    public boolean isWonAfterNoGamePieces(Spielstand ss) {
        return true;
    }
}
