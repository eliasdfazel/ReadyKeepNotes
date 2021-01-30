package net.geeksempire.ready.keep.notes.Utils.UI.PopupOptionsMenu

import android.view.View

class BalloonOptionsMenu (private val anchorView: View) {

    val positionXY = IntArray(2)

    var viewX = 0
    var viewY = 0

    val rootViewWidth = anchorView.width
    val rootViewHeight = anchorView.height

    fun initializeBalloonPosition() {

        anchorView.getLocationInWindow(positionXY)

        viewX = positionXY[0]
        viewY = positionXY[1]



    }

}