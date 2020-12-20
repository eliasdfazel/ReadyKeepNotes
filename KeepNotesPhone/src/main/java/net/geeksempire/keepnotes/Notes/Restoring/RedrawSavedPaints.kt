package net.geeksempire.keepnotes.Notes.Restoring

import kotlinx.coroutines.*
import net.geeksempire.keepnotes.Notes.Tools.Painting.PaintingCanvasView
import net.geeksempire.keepnotes.Notes.Tools.Painting.RedrawPaintingData

class RedrawSavedPaints (private val paintingCanvasView: PaintingCanvasView) {

    fun start(allRedrawPaintingData: ArrayList<RedrawPaintingData>) = CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {

        delay(1000)

        paintingCanvasView.touchingStartRestore(allRedrawPaintingData[0].xDrawPosition, allRedrawPaintingData[0].yDrawPosition)

        paintingCanvasView.touchingMoveRestore(allRedrawPaintingData[0].xDrawPosition, allRedrawPaintingData[0].yDrawPosition)

        allRedrawPaintingData.forEach { redrawPaintingData ->

            paintingCanvasView.touchingMoveRestore(redrawPaintingData.xDrawPosition, redrawPaintingData.yDrawPosition)

        }

        paintingCanvasView.touchingUpRestore()

    }

}