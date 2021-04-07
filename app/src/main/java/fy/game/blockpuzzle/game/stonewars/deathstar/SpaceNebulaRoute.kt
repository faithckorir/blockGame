package fy.game.blockpuzzle.game.stonewars.deathstar

import fy.game.blockpuzzle.cluster.AbstractRoute
import fy.game.blockpuzzle.global.GlobalData
import fy.game.blockpuzzle.playingfield.Action

/**
 * Route through the space nebula leads to the Death Star, which threatens the Milky Way.
 */
class SpaceNebulaRoute(from: Int, to: Int, private val alertAction: Action) : AbstractRoute(from, to) {

    override fun travel(): Boolean {
        DeathStarReseter().startGame()

        // show Milky Way alert
        alertAction.execute()
        // and after that the game will be displayed immediately
        return false
    }

    companion object {
        val isNoDeathStarMode: Boolean
            get() = GlobalData.get().todesstern != 1
    }
}