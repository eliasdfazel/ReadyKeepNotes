package net.geeksempire.keepnotes.Notes.Restoring

import android.util.Log
import kotlinx.coroutines.*
import net.geeksempire.keepnotes.Notes.Tools.Painting.PaintingCanvasView
import net.geeksempire.keepnotes.Notes.Tools.Painting.RedrawPaintingData

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
            allRedrawPaintingPathData[0].xDrawPosition,
            allRedrawPaintingPathData[0].yDrawPosition,
            allRedrawPaintingPathData[0].paintColor,
            allRedrawPaintingPathData[0].paintStrokeWidth)

        paintingCanvasView.touchingMoveRestore(allRedrawPaintingPathData[0].xDrawPosition, allRedrawPaintingPathData[0].yDrawPosition)

        allRedrawPaintingPathData.forEachIndexed paintingLoop@ { index, redrawPaintingData ->

            paintingCanvasView.touchingMoveRestore(redrawPaintingData.xDrawPosition, redrawPaintingData.yDrawPosition)

        }

        paintingCanvasView.touchingUpRestore(allRedrawPaintingPathData[0].paintColor, allRedrawPaintingPathData[0].paintStrokeWidth)

    }

}