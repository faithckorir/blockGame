package fy.game.blockpuzzle.game.stonewars;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import fy.game.blockpuzzle.game.GameEngineBuilder;
import fy.game.blockpuzzle.game.GameEngineModel;
import fy.game.blockpuzzle.game.place.IPlaceAction;
import fy.game.blockpuzzle.game.stonewars.place.Check4VictoryPlaceAction;
import fy.game.blockpuzzle.gamedefinition.GameDefinition;
import fy.game.blockpuzzle.gamestate.GameState;
import fy.game.blockpuzzle.gamestate.StoneWarsGameState;
import fy.game.blockpuzzle.playingfield.PlayingField;

/**
 * Initialization of StoneWarsGameEngine
 */
public class StoneWarsGameEngineBuilder extends GameEngineBuilder {

    // create data ----

    @NotNull
    @Override
    protected GameState createGameState() {
        return StoneWarsGameState.create();
    }

    @Override
    protected List<IPlaceAction> createPlaceActions() {
        List<IPlaceAction> list = super.createPlaceActions();
        list.add(getCheck4VictoryPlaceAction());
        return list;
    }

    @NotNull
    protected Check4VictoryPlaceAction getCheck4VictoryPlaceAction() {
        return new Check4VictoryPlaceAction();
    }

    @Override
    protected void initPlayingField(PlayingField playingField) {
        super.initPlayingField(playingField);
        ((GameDefinition) definition).fillStartPlayingField(playingField);
    }

    // create game engine ----

    @NotNull
    protected StoneWarsGameEngine createGameEngine(GameEngineModel model) {
        return new StoneWarsGameEngine(model);
    }

    // new game ----

    @Override
    protected void initNextGamePieceForNewGame() {
        if (((StoneWarsGameState) gs).getPlanet().isNextGamePieceResetedForNewGame()) {
            nextGamePiece.reset();
        } else { // Daily Planet
            nextGamePiece.load();
        }
    }

    // load game ----

    @Override
    protected void loadGame() {
        super.loadGame();
        if (gs.isLostGame()) {
            gameEngine.showScoreAndMoves(); // display game over text
            playingField.gameOver();
        }
    }
}
