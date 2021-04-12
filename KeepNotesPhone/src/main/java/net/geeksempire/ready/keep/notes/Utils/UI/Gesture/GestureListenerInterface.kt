/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Utils.UI.Gesture

import android.view.MotionEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview

interface GestureListenerInterface {
    fun onSwipeGesture(gestureConstants: GestureConstants, downMotionEvent: MotionEvent, moveMotionEvent: MotionEvent, initVelocityX: Float, initVelocityY: Float) {}

    fun onSingleTapUp(motionEvent: MotionEvent) {}
    fun onLongPress(motionEvent: MotionEvent) {}
}

interface SwipeActions {
    /**
     *
     **/
    fun onSwipeToStart(context: KeepNoteOverview, position: Int) = CoroutineScope(Dispatchers.Main).launch {}
    /**
     *
     **/
    fun onSwipeToEnd(context: KeepNoteOverview, position: Int) = CoroutineScope(Dispatchers.Main).launch {}
}

interface UnderlayOptionsActions {
    fun onClick() = CoroutineScope(Dispatchers.Main).launch {  }
}