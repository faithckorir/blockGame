package fy.game.blockpuzzle.game

import android.content.ClipDescription
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import fy.game.blockpuzzle.R
import fy.game.blockpuzzle.gamepiece.GamePiece
import fy.game.blockpuzzle.gamepiece.GamePieceTouchListener
import fy.game.blockpuzzle.gamepiece.GamePieceView
import fy.game.blockpuzzle.gamestate.SpielstandDAO
import fy.game.blockpuzzle.global.AbstractDAO
import fy.game.blockpuzzle.global.BridgeActivity
import fy.game.blockpuzzle.global.GlobalData
import fy.game.blockpuzzle.global.InfoActivity
import fy.game.blockpuzzle.global.messages.MessageFactory
import fy.game.blockpuzzle.playingfield.Action
import fy.game.blockpuzzle.playingfield.IPlayingFieldView
import fy.game.blockpuzzle.playingfield.PlayingFieldView
import fy.game.blockpuzzle.playingfield.QPosition
import fy.game.blockpuzzle.playingfield.gravitation.ShakeService
import kotlinx.android.synthetic.main.activity_main.*

/**
 * GameActivity
 *
 * It has still the name MainActivity because I'm not sure how to change the name
 * without destroying everything.
 */
class MainActivity : AppCompatActivity(), IGameView {
    private lateinit var gameEngine: GameEngine
    private lateinit var shakeService : ShakeService
    private var messages: MessageFactory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // INIT PHASE
        if (Build.VERSION.SDK_INT >= 21) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.navigationBackground)
        }

        // SUPER PHASE
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AbstractDAO.init(this)

        // REST OF METHOD PHASE
        (placeholder1 as ViewGroup).addView(GamePieceView(baseContext, 1, false))
        (placeholder2 as ViewGroup).addView(GamePieceView(baseContext, 2, false))
        (placeholder3 as ViewGroup).addView(GamePieceView(baseContext, 3, false))
        (parking      as ViewGroup).addView(GamePieceView(baseContext, -1, true))

        initTouchListener(1)
        initTouchListener(2)
        initTouchListener(3)
        initTouchListener(-1)
        playingField.setOnDragListener(createDragListener(false)) // Drop Event f??r Spielfeld
        parking.setOnDragListener(createDragListener(true)) // Drop Event f??rs Parking
        initNewGameButton()
    }

    // Activity reactivated
    override fun onResume() {
        super.onResume()
        try {
            initGameEngine()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, e.javaClass.toString() + ": " + e.message + "\n" + e.stackTrace[0].toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun initGameEngine() {
        gameEngine = GameEngineFactory().create(this)

        shakeService = ShakeService(gameEngine)
        shakeService.setActive(true)
        shakeService.initShakeDetection(this)

        newGame.visibility = when (gameEngine.isNewGameButtonVisible) {
            true  -> View.VISIBLE
            false -> View.INVISIBLE
        }
    }

    // Activity goes sleeping
    override fun onPause() {
        gameEngine.save()

        shakeService.setActive(false)

        super.onPause()
    }

    /** Drag or rotate game piece */
    private fun initTouchListener(index: Int) {
        getGamePieceView(index).setOnTouchListener(object : GamePieceTouchListener(index, resources) {
            override fun up(view: View?, event: MotionEvent?) {
                getGamePieceView(index).endDragMode()
                gameEngine.rotate(index)
            }

            override fun isDragAllowed(): Boolean {
                return !gameEngine.isLostGame && gameEngine.isDragAllowed
            }
        })
    }

    /** mainly for dropping the game piece */
    private fun createDragListener(targetIsParking: Boolean): View.OnDragListener {
        return View.OnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }
                DragEvent.ACTION_DROP -> dropped(event, targetIsParking)
                DragEvent.ACTION_DRAG_ENTERED -> {
                    if (targetIsParking) {
                        getGamePieceView(-1).onDragEnter()
                    }
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> true
                DragEvent.ACTION_DRAG_EXITED -> {
                    if (targetIsParking) {
                        getGamePieceView(-1).onDragLeave()
                    }
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    try {
                        if (targetIsParking) {
                            getGamePieceView(-1).onDragLeave()
                        }
                        // Da ich nicht wei??, welcher ausgeblendet ist, blende ich einfach alle ein.
                        getGamePieceView(1).endDragMode()
                        getGamePieceView(2).endDragMode()
                        getGamePieceView(3).endDragMode()
                        getGamePieceView(-1).endDragMode()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "F/dragEnd: " + e.message, Toast.LENGTH_LONG).show()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun dropped(event: DragEvent, targetIsParking: Boolean): Boolean {
        try {
            val item = event.clipData.getItemAt(0)
            val index: Int = item.text.toString().toInt()
            val gamePiece = getGamePieceView(index).gamePiece
            var xy : QPosition = QPosition(-1, -1)

            if (!targetIsParking) {
                // geg.: px, ges.: SpielfeldView Koordinaten (0 - 9)
                xy = calculatePlayingFieldCoordinates(event, gamePiece)
            }
            gameEngine.dispatch(targetIsParking, DropActionModel(index, gamePiece, xy))

            if (gameEngine.rebuild) {
                gameEngine.rebuild = false;
                gameEngine.isDragAllowed = false // will be set to true in GameEngine construction
                @Suppress("DEPRECATION")
                Handler().postDelayed({ initGameEngine() }, 1200)
            }
        } catch (e: DoesNotWorkException) {
            playingField.soundService.doesNotWork()
            Toast.makeText(this, R.string.gehtNicht, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "MA:" + e.message, Toast.LENGTH_LONG).show()
        }
        return true
    }

    private fun calculatePlayingFieldCoordinates(event: DragEvent, gamePiece: GamePiece): QPosition {
        val f = resources.displayMetrics.density
        var x = event.x / f // px -> dp
        var y = event.y / f
        // jetzt in Spielfeld Koordinaten umrechnen
        val br = PlayingFieldView.w / gameEngine.blocks
        x /= br
        y = y / br - 2 - (gamePiece.maxY - gamePiece.minY)
        return QPosition(x.toInt(), y.toInt())
    }

    private fun initNewGameButton() {
        newGame.setOnClickListener {
            if (gameEngine.isLostGame || gameEngine.lessScore()) {
                startNewGame()
            } else {
                val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
                dialog.setTitle(resources.getString(R.string.startNewGame))
                dialog.setPositiveButton(resources.getString(android.R.string.ok)) { _, _ -> startNewGame() }
                dialog.setNegativeButton(resources.getString(android.R.string.cancel), null)
                dialog.show()
            }
        }
    }

    private fun startNewGame() {
        SpielstandDAO().deleteOldGame()
        initGameEngine()
    }

    override fun getSpecialAction(specialState: Int): Action {
        if (specialState == 2) { // Death Star destroyed
            return Action {
                val intent = Intent(this, InfoActivity::class.java)
                val args = Bundle()
                args.putInt(InfoActivity.MODE, InfoActivity.BACK_FROM_DEATH_STAR)
                intent.putExtras(args)
                startActivity(intent)
            }
        }
        return Action {}
    }

    override fun showScore(text: String) {
        info.text = text
    }

    override fun showMoves(text: String) {
        infoDisplay.text = text
    }

    override fun showTerritoryName(resId: Int) {
        val text = resources.getText(resId).trim()
        territoryName.text = text
        territoryName.visibility = if (text.isEmpty()) View.GONE else View.VISIBLE // hide label to save space
    }

    override fun getPlayingFieldView(): IPlayingFieldView {
        return playingField
    }

    override fun getGamePieceView(index: Int): GamePieceView {
        return when (index) {
            1 -> (placeholder1 as ViewGroup).getChildAt(0) as GamePieceView
            2 -> (placeholder2 as ViewGroup).getChildAt(0) as GamePieceView
            3 -> (placeholder3 as ViewGroup).getChildAt(0) as GamePieceView
            -1 -> (parking     as ViewGroup).getChildAt(0) as GamePieceView
            else -> throw RuntimeException()
        }
    }

    override fun onBackPressed() {
        if (GlobalData.get().todesstern == 1) {
            if (gameEngine.isDragAllowed) { // during wait time back pressing is not allowed, game state could get unstable
                startActivity(Intent(this, BridgeActivity::class.java))
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun shake() {
        playingField.soundService.shake()
    }

    override fun playSound(number: Int) {
        playingField.soundService.playSound(number)
    }

    override fun getMessages(): MessageFactory {
        if (messages == null) {
            messages = MessageFactory(this)
        }
        return messages!!
    }
}
