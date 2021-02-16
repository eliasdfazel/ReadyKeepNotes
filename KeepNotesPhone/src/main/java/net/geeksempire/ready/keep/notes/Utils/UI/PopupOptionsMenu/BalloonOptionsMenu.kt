package net.geeksempire.ready.keep.notes.Utils.UI.PopupOptionsMenu

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.Display.DpToInteger
import net.geeksempire.ready.keep.notes.Utils.UI.Display.displayX

class BalloonOptionsMenu (private val context: AppCompatActivity, private val rootView: ViewGroup, private val anchorView: View) {

    private val balloonView = LayoutInflater.from(context).inflate(R.layout.balloon_options_menu_layout, null)

    private val positionXY = IntArray(2)

    var viewX = 0
    var viewY = 0

    val anchorViewWidth = anchorView.width
    val anchorViewHeight = anchorView.height

    fun initializeBalloonPosition() : BalloonOptionsMenu {

        anchorView.getLocationInWindow(positionXY)

        viewX = positionXY[0]
        viewY = positionXY[1]
        Log.d(this@BalloonOptionsMenu.javaClass.simpleName, "TouchX: ${viewX} | TouchY: ${viewY}")


        rootView.addView(balloonView)

        val balloonLayoutParams = balloonView.layoutParams as ConstraintLayout.LayoutParams

        balloonView.x = (displayX(context) / 2).toFloat() - DpToInteger(context, 75)
        balloonView.y = viewY.toFloat()

        return this@BalloonOptionsMenu
    }

    fun setupOptionsItems(titlesOfItems: ArrayList<String>) {

        titlesOfItems.forEach {



        }

    }

}