package net.geeksempire.ready.keep.notes.Utils.UI.PopupTutorial

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.Display.displayX
import net.geeksempire.ready.keep.notes.Utils.UI.Display.dpToInteger

interface BalloonItemsAction {
    fun onBalloonItemClickListener(balloonOptionsRootView: View, view: View)
}

class BalloonTutorials (private val context: AppCompatActivity,
                        private val rootView: ViewGroup,
                        private val clickListener: BalloonItemsAction) {

    private val balloonOptionsRootView = LayoutInflater.from(context).inflate(R.layout.balloon_options_menu_layout, null)
    private val allItemsView = balloonOptionsRootView.findViewById<LinearLayout>(R.id.allItemsView)

    private val positionXY = IntArray(2)

    var viewX = 0
    var viewY = 0

    var balloonOptionsAdded = false

    fun initializeBalloonPosition(anchorView: View) : BalloonTutorials {

        anchorView.getLocationInWindow(positionXY)

        viewX = positionXY[0]
        viewY = positionXY[1]
        Log.d(this@BalloonTutorials.javaClass.simpleName, "TouchX: ${viewX} | TouchY: ${viewY}")

        if (balloonOptionsAdded) {

            balloonOptionsAdded = false

            rootView.removeView(balloonOptionsRootView)

        }

        rootView.addView(balloonOptionsRootView)
        balloonOptionsRootView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))

        val balloonLayoutParams = balloonOptionsRootView.layoutParams as ConstraintLayout.LayoutParams

        balloonOptionsRootView.x = (displayX(context) / 2).toFloat() - dpToInteger(context, 75)
        balloonOptionsRootView.y = viewY.toFloat()

        balloonOptionsAdded = true

        balloonOptionsRootView.setOnFocusChangeListener { view, hasFocus ->


        }

        return this@BalloonTutorials
    }

    fun setupOptionsItems(titlesOfItems: Array<String>) {

        titlesOfItems.forEach {

            val itemLayout = LayoutInflater.from(context).inflate(R.layout.balloon_option_item, null)
            val balloonOptionItemTextView = itemLayout.findViewById<TextView>(R.id.balloonOptionItemTextView)

            balloonOptionItemTextView.text = it
            balloonOptionItemTextView.tag = it

            itemLayout.setOnClickListener { view ->

                clickListener.onBalloonItemClickListener(balloonOptionsRootView, view)

            }

            allItemsView.addView(itemLayout)

        }

    }

    fun removeBalloonOption() {

        if (balloonOptionsAdded) {

            balloonOptionsAdded = false

            rootView.removeView(balloonOptionsRootView)

        }

    }

}