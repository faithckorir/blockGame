package fy.game.blockpuzzle.game.place;

import java.util.List;

import fy.game.blockpuzzle.block.special.ISpecialBlock;
import fy.game.blockpuzzle.gamepiece.GamePiece;
import fy.game.blockpuzzle.playingfield.QPosition;

public class SendPlacedEventAction implements IPlaceAction {

    @Override
    public void perform(PlaceActionModel info) {
        GamePiece gamePiece = info.getGamePiece();
        List<ISpecialBlock> specialBlocks = info.getBlockTypes().getSpecialBlockTypes();
        for (int x = gamePiece.getMinX(); x <= gamePiece.getMaxX(); x++) {
            for (int y = gamePiece.getMinY(); y <= gamePiece.getMaxY(); y++) {
                if (gamePiece.filled(x, y)) {
                    int bt = gamePiece.getBlockType(x, y);
                    for (ISpecialBlock s : specialBlocks) {
                        if (s.getBlockType() == bt) {
                            s.placed(gamePiece, info.getPosition(), new QPosition(x, y));
                        }
                    }
                }
            }
        }
    }
}
