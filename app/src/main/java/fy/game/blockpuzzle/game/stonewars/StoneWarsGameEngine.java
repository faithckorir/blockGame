package fy.game.blockpuzzle.game.stonewars;

import fy.game.blockpuzzle.game.GameEngine;
import fy.game.blockpuzzle.game.GameEngineModel;
import fy.game.blockpuzzle.gamestate.StoneWarsGameState;

/**
 * Stone Wars game engine
 */
public class StoneWarsGameEngine extends GameEngine {

    public StoneWarsGameEngine(GameEngineModel model) {
        super(model);
    }

    @Override
    public boolean isNewGameButtonVisible() {
        return false;
    }

    @Override
    protected boolean isHandleNoGamePiecesAllowed() {
        return true;
    }

    @Override
    public void onEndGame(boolean wonGame, boolean stopGame) {
        super.onEndGame(wonGame, stopGame);
        if (!wonGame) { // lost game
            ((StoneWarsGameState) gs).saveOwner(false); // owner is Orange Union or enemy
        }
    }
}
