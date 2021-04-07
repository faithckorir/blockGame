package fy.game.blockpuzzle.gamedefinition;

import androidx.annotation.NonNull;

import fy.game.blockpuzzle.gamepiece.INextGamePiece;
import fy.game.blockpuzzle.gamepiece.NextGamePieceFromSet;
import fy.game.blockpuzzle.gamestate.GameState;
import fy.game.blockpuzzle.gamestate.ScoreChangeInfo;
import fy.game.blockpuzzle.gamestate.Spielstand;
import fy.game.blockpuzzle.global.messages.MessageObjectWithGameState;
import fy.game.blockpuzzle.playingfield.PlayingField;

/**
 * Stone Wars game definition
 */
public abstract class GameDefinition extends OldGameDefinition {
    private final int gamePieceSetNumber;
    /** R.string constant */
    private int territoryName;
    private LiberatedFeature libf = null;

    public GameDefinition(int gamePieceSetNumber) {
        this.gamePieceSetNumber = gamePieceSetNumber;
    }

    // GAME DEFINITION ----

    @Override
    public INextGamePiece createNextGamePieceGenerator(GameState gs) {
        return new NextGamePieceFromSet(gamePieceSetNumber, gs);
    }

    public int getGamePieceSetNumber() {
        return gamePieceSetNumber;
    }

    public int getTerritoryName() {
        return territoryName;
    }

    public void setTerritoryName(int territoryName) {
        this.territoryName = territoryName;
    }

    /**
     * @return true: Sieg, false: nicht relevant oder kein Sieg
     */
    public boolean wonGameIfPlayingFieldIsEmpty() {
        return false;
    }

    /**
     * @return true: Die Moves-Anzeige der Score-Anzeige bevorzugen. false: andersrum
     */
    public boolean showMoves() {
        return false;
    }


    // GAME INIT PHASE ----

    /**
     * call p.draw() after filling something in
     */
    public void fillStartPlayingField(PlayingField p) { // Template method
    }


    // DISPLAY ----

    /**
     * Returns short game description
     * @param longDisplay true: normal length, false: a bit shorter
     * @return e.g. "CLassic Game MLS8k"
     */
    public abstract String getDescription(boolean longDisplay);


    // QUESTIONS AND EVENTS ----

    /**
     * @return true if planet or territory was liberated by player 1
     */
    public abstract boolean isLiberated(ILiberatedInfo info);

    /**
     * @param info data and services
     * @return MessageObjectWithGameState; info.messages.noMessage if there's no message to be displayed; not null
     */
    @NonNull
    public abstract MessageObjectWithGameState scoreChanged(ScoreChangeInfo info);

    public LiberatedFeature getLiberatedFeature() {
        return libf;
    }

    public void setLiberatedFeature(LiberatedFeature v) {
        libf = v;
    }

    /**
     * If there are no game pieces anymore the player has lost the game by default.
     * If you return true here you can let the player win the game.
     */
    public boolean isWonAfterNoGamePieces(Spielstand ss) {
        return false;
    }
}
