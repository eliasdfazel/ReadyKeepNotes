package net.geeksempire.ready.keep.notes.Notes.Taking.Extensions

import android.text.Html
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.NewPaintingData
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Line Stroke Width Changer
 **/
fun TakeNote.paintingActionsStrokeWidthSample() {

    val fluidSliderMinimum = 3.0.toFloat()
    val fluidSlideMaximum = 33.0.toFloat()
    val totalFluidSliderAmount = (fluidSlideMaximum - fluidSliderMinimum)

    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.positionListener = { fluidSliderPosition ->

        val decimalFormat = DecimalFormat("#.##")
        decimalFormat.roundingMode = RoundingMode.HALF_EVEN

        val selectedStrokeWidth = decimalFormat.format((fluidSliderMinimum + (totalFluidSliderAmount  * fluidSliderPosition))).toFloat()

        takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.bubbleText = Html.fromHtml("<small>${selectedStrokeWidth.toString()}</small>", Html.FROM_HTML_MODE_COMPACT).toString()

        paintingCanvasView.changePaintingPathStrokeWidth(NewPaintingData(paintColor = paintingCanvasView.newPaintingData.paintColor, paintStrokeWidth = selectedStrokeWidth))

        strokePaintingCanvasView.changePaintingPathStrokeWidth(NewPaintingData(paintColor = strokePaintingCanvasView.newPaintingData.paintColor, paintStrokeWidth = selectedStrokeWidth))

    }

    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.position = 0f

//    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.startText ="$fluidSliderMinimum"
//    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.endText = "$fluidSlideMaximum"

    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.beginTrackingListener = {



    }

    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.endTrackingListener = {

        strokePaintingCanvasView.removeAllPaints()


    }

}