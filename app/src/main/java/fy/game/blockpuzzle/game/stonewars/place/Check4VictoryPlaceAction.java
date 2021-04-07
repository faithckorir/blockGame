package fy.game.blockpuzzle.game.stonewars.place;

import fy.game.blockpuzzle.game.GameEngineInterface;
import fy.game.blockpuzzle.game.GameInfoService;
import fy.game.blockpuzzle.game.place.IPlaceAction;
import fy.game.blockpuzzle.game.place.PlaceActionModel;
import fy.game.blockpuzzle.gamedefinition.SSLiberatedInfo;
import fy.game.blockpuzzle.gamestate.GamePlayState;
import fy.game.blockpuzzle.gamestate.ScoreChangeInfo;
import fy.game.blockpuzzle.gamestate.StoneWarsGameState;
import fy.game.blockpuzzle.global.messages.MessageObjectWithGameState;
import fy.game.blockpuzzle.planet.IPlanet;

/**
 * Stone Wars: Prüfungen ob Spiel gewonnen oder verloren.
 * Weiterhin Behandlung für den Fall, dass keine Spielsteine mehr verfügbar sind.
 */
public class Check4VictoryPlaceAction implements IPlaceAction {
    // TODO Prüfen, ob ich Code von hier in die XXXGameDefinition verschieben kann. Classic/Cleaner-spezifisches soll hier ja nicht stehen.

    @Override
    public void perform(PlaceActionModel info) {
        // check4Victory: // Spielsiegprüfung (showScore erst danach)
        StoneWarsGameState swgs = (StoneWarsGameState) info.getGs();
        final GamePlayState oldState = swgs.get().getState();
        ScoreChangeInfo scInfo = new ScoreChangeInfo(swgs, info.getMessages());

        // 1st verification ----
        MessageObjectWithGameState msg = swgs.getDefinition().scoreChanged(scInfo);
        if (oldState == GamePlayState.PLAYING) {
            msg.show();
            boolean stopGame = true;
            if (msg.isWonGame()) { // z.B. wenn Mindestscore erreicht
                check4Liberation(info.getGameEngineInterface(), swgs);
                stopGame = !swgs.getDefinition().gameGoesOnAfterWonGame();
                info.getGameEngineInterface().onEndGame(true, stopGame);
                if (stopGame) return;
            } else if (msg.isLostGame()) { // z.B. wenn zu viele Moves
                info.getGameEngineInterface().onEndGame(false, true);
                return;
            }
        }

        // 2nd verification ----
        if (info.getPlayingField().getFilled() == 0 && swgs.getDefinition().wonGameIfPlayingFieldIsEmpty()) {
            gameOverOnEmptyPlayingField(info);
        }
    }

    protected void check4Liberation(GameEngineInterface game, StoneWarsGameState gs) {
        game.save();
        IPlanet planet = gs.getPlanet();
        if (new GameInfoService().isPlanetFullyLiberated(planet)) {
            new GameInfoService().executeLiberationFeature(planet);
        }
    }

    private void gameOverOnEmptyPlayingField(PlaceActionModel info) {
        StoneWarsGameState swgs = (StoneWarsGameState) info.getGs();
        info.getGameEngineInterface().clearAllHolders();
        info.getPlayingField().gameOver();
        if (isLiberated(swgs)) {
            // Folgende Aktionen dürfen nur bei einem 1-Game-Planet gemacht werden! Ein Cleaner Game wird aber
            // auch nur bei 1-Game-Planets angeboten. Daher passt das.
            swgs.setOwnerToMe();
            check4Liberation(info.getGameEngineInterface(), swgs);
        }
        info.getGameEngineInterface().onEndGame(true, true);
        info.getMessages().getPlanetLiberated().show();
    }

    private boolean isLiberated(StoneWarsGameState swgs) {
        SSLiberatedInfo libInfo = new SSLiberatedInfo(swgs.get()) {
            @Override
            public boolean isPlayingFieldEmpty() {
                return true; // playing field is really empty
            }
        };
        return swgs.getDefinition().isLiberated(libInfo);
    }
}
