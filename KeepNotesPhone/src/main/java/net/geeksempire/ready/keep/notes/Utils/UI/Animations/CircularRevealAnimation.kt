package net.geeksempire.ready.keep.notes.Utils.UI.Animations

import android.animation.Animator
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import net.geeksempire.ready.keep.notes.Utils.UI.Display.DpToInteger
import net.geeksempire.ready.keep.notes.Utils.UI.Display.displayX
import net.geeksempire.ready.keep.notes.Utils.UI.Display.displayY
import kotlin.math.hypot

interface AnimationListener {
    fun animationFinished() {}
}

class CircularRevealAnimation (private val animationListener: AnimationListener) {

    fun startForActivityRoot(activity: AppCompatActivity, rootView: View, xPosition: Int = (displayX(activity) / 2), yPosition: Int = (displayY(activity) / 2)) {

        val rootLayout = rootView
        rootLayout.visibility = View.INVISIBLE

        val viewTreeObserver = rootLayout.viewTreeObserver

        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

                override fun onGlobalLayout() {

                    val finalRadius = hypot(displayX(activity).toDouble(), displayY(activity).toDouble())

                    val circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout,
                        xPosition,
                        yPosition,
                        DpToInteger(activity, 51).toFloat(),
                        finalRadius.toFloat())

                    circularReveal.duration = 1111
                    circularReveal.interpolator = AccelerateInterpolator()

                    rootLayout.visibility = View.VISIBLE
                    circularReveal.start()

                    rootLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    circularReveal.addListener(object : Animator.AnimatorListener {

                        override fun onAnimationRepeat(animation: Animator?) {

                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            rootLayout.visibility = View.VISIBLE

                            animationListener.animationFinished()

                        }

                        override fun onAnimationCancel(animation: Animator?) {

                        }

                        override fun onAnimationStart(animation: Animator?) {

                        }

                    })
                }
            })
        } else {
            rootLayout.visibility = View.VISIBLE
        }

    }

    fun startForView() {



    }

}