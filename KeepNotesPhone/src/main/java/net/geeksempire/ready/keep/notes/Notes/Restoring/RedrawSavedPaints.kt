package net.geeksempire.ready.keep.notes.Notes.Restoring

import android.util.Log
import kotlinx.coroutines.*
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Extensions.touchingMoveRestore
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Extensions.touchingStartRestore
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Extensions.touchingUpRestore
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.PaintingCanvasView
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.RedrawPaintingData
import net.geeksempire.ready.keep.notes.Utils.UI.Display.displayX
import net.geeksempire.ready.keep.notes.Utils.UI.Display.displayY
import net.geeksempire.ready.keep.notes.Utils.UI.Display.percentageToPixel

class RedrawSavedPaints (private val paintingCanvasView: PaintingCanvasView) {

    fun runRestoreProcess(allRedrawPaintingData: ArrayList<ArrayList<RedrawPaintingData>>) = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {

        allRedrawPaintingData.forEach { paintingPathData ->

            startPainting(paintingPathData)

        }

    }

    private fun startPainting(allRedrawPaintingPathData: ArrayList<RedrawPaintingData>) = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
        Log.d(this@RedrawSavedPaints.javaClass.simpleName, "${allRedrawPaintingPathData[0].xDrawPosition} | ${allRedrawPaintingPathData[0].yDrawPosition}")

        delay(1133)

        paintingCanvasView.touchingStartRestore(
            allRedrawPaintingPathData[0].xDrawPosition.percentageToPixel(displayX(paintingCanvasView.context).toFloat()),
            allRedrawPaintingPathData[0].yDrawPosition.percentageToPixel(displayY(paintingCanvasView.context).toFloat()),
            allRedrawPaintingPathData[0].paintColor,
            allRedrawPaintingPathData[0].paintStrokeWidth)

        paintingCanvasView.touchingMoveRestore(
            allRedrawPaintingPathData[0].xDrawPosition.percentageToPixel(displayX(paintingCanvasView.context).toFloat()),
            allRedrawPaintingPathData[0].yDrawPosition.percentageToPixel(displayY(paintingCanvasView.context).toFloat())
        )

        allRedrawPaintingPathData.forEachIndexed paintingLoop@ { index, redrawPaintingData ->

            paintingCanvasView.touchingMoveRestore(
                redrawPaintingData.xDrawPosition.percentageToPixel(displayX(paintingCanvasView.context).toFloat()),
                redrawPaintingData.yDrawPosition.percentageToPixel(displayY(paintingCanvasView.context).toFloat())
            )

        }

        paintingCanvasView.touchingUpRestore(allRedrawPaintingPathData[0].paintColor, allRedrawPaintingPathData[0].paintStrokeWidth)

    }

}