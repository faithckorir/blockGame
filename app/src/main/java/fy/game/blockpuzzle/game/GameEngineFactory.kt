package fy.game.blockpuzzle.game

import fy.game.blockpuzzle.cluster.Cluster1
import fy.game.blockpuzzle.game.stonewars.StoneWarsGameEngineBuilder
import fy.game.blockpuzzle.game.stonewars.deathstar.DeathStarGameEngineBuilder
import fy.game.blockpuzzle.game.stonewars.deathstar.MilkyWayCluster
import fy.game.blockpuzzle.global.GameType
import fy.game.blockpuzzle.global.GlobalData
import fy.game.blockpuzzle.planet.IPlanet

class GameEngineFactory {

    /**
     * Returns initialized game engine
     */
    fun create(view: IGameView): GameEngine {
        val gd = GlobalData.get()
        val builder = if (gd.gameType == GameType.STONE_WARS) {
            if (gd.todesstern == 1) {
                DeathStarGameEngineBuilder()
            } else {
                StoneWarsGameEngineBuilder()
            }
        } else {
            GameEngineBuilder() // old game
        }
        return builder.build(view)
    }

    /**
     * Stone Wars: returns the current planet where the spaceship/player is
     */
    fun getPlanet(): IPlanet {
        val gd = GlobalData.get()
        if  (gd.todesstern == 1) { // Death Star game active
            val planet = MilkyWayCluster.get()
            planet.selectedGame = planet.gameDefinitions[0]
            return planet
        }
        if (gd.currentPlanet == 100) gd.currentPlanet = 1 // HACK
        return Cluster1.spaceObjects.first { it.number == gd.currentPlanet } as IPlanet
    }
}
