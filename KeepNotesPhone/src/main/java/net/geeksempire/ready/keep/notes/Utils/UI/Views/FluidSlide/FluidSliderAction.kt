/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Utils.UI.Views.FluidSlide

import android.util.Log

interface FluidSliderActionInterface {
    fun fluidSliderPosition(sliderPosition: Float) {}

    fun fluidSliderStarted() {}
    fun fluidSliderFinished() {}
}

class FluidSliderAction (private val fluidSliderActionInterface: FluidSliderActionInterface) {

    fun startListener(fluidSlider: FluidSlider) {

        fluidSlider.positionListener = { sliderPosition ->
            Log.d(this@FluidSliderAction.javaClass.simpleName, "$sliderPosition")

            fluidSlider.bubbleText = "${0 + (7  * sliderPosition).toInt()}"

            fluidSliderActionInterface.fluidSliderPosition(sliderPosition)
        }

        fluidSlider.beginTrackingListener = {

            fluidSliderActionInterface.fluidSliderStarted()

        }

        fluidSlider.endTrackingListener = {

            fluidSliderActionInterface.fluidSliderFinished()

        }

    }

}