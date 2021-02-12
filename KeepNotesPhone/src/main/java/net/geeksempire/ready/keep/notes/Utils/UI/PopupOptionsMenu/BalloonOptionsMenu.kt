package net.geeksempire.ready.keep.notes.Utils.UI.PopupOptionsMenu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import net.geeksempire.ready.keep.notes.R

class BalloonOptionsMenu (private val context: Context, private val rootView: ViewGroup, private val anchorView: View) {

    val positionXY = IntArray(2)

    var viewX = 0
    var viewY = 0

    val rootViewWidth = anchorView.width
    val rootViewHeight = anchorView.height

    fun initializeBalloonPosition() {

        anchorView.getLocationInWindow(positionXY)

        viewX = positionXY[0]
        viewY = positionXY[1]

        val balloonView = LayoutInflater.from(context).inflate(R.layout.balloon_options_menu_layout, null)

        val balloonLayoutParams = balloonView.layoutParams as ConstraintLayout.LayoutParams

        rootView.addView(balloonView)

        balloonView.translationX = viewX.toFloat()
        balloonView.translationY = viewY.toFloat()

    }

}