package net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Extensions

import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.NewPaintingData
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.PaintingCanvasView
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.PaintingData
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.RedrawPaintingData
import kotlin.math.abs

fun PaintingCanvasView.touchingStart(x: Float, y: Float) {

    undoDrawingInformation.clear()

    drawingPath.reset()
    drawingPath.moveTo(x, y)

    movingX = x
    movingY = y

    //Set New Color To Current Paint
    drawPaint.color = newPaintingData.paintColor
    drawPaint.strokeWidth = newPaintingData.paintStrokeWidth

    newPaintingData.paint?.let {
        drawPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
    }

    allRedrawPaintingPathData = ArrayList<RedrawPaintingData>()
    allRedrawPaintingPathData.clear()

    allRedrawPaintingPathData.add(0, RedrawPaintingData(x, y, newPaintingData.paintColor, newPaintingData.paintStrokeWidth))
    allRedrawPaintingPathData.add(RedrawPaintingData(x, y, newPaintingData.paintColor, newPaintingData.paintStrokeWidth))

    invalidate()

}

fun PaintingCanvasView.touchingMove(x: Float, y: Float) {

    allRedrawPaintingPathData.add(RedrawPaintingData(x, y, newPaintingData.paintColor, newPaintingData.paintStrokeWidth))

    val dX: Float = abs(x - movingX)
    val dY: Float = abs(y - movingY)

    if (dX >= touchTolerance || dY >= touchTolerance) {

        drawingPath.quadTo(movingX, movingY, (x + movingX) / 2, (y + movingY) / 2)

        movingX = x
        movingY = y

    }

    invalidate()

}

fun PaintingCanvasView.touchingUp() {

    allRedrawPaintingPathData.add(RedrawPaintingData(movingX, movingY, newPaintingData.paintColor, newPaintingData.paintStrokeWidth))

    overallRedrawPaintingData.add(allRedrawPaintingPathData)

    drawingPath.lineTo(movingX, movingY)

    //Set New Color To New Paint
    val newPaintObject = Paint(drawPaint)
    newPaintObject.color = newPaintingData.paintColor
    newPaintObject.strokeWidth = newPaintingData.paintStrokeWidth

    newPaintingData.paint?.let {
        newPaintObject.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    allDrawingInformation.add(PaintingData(paint = newPaintObject, path = drawingPath))

    drawingPath = Path()

    invalidate()

}

fun PaintingCanvasView.changePaintingData(modifiedNewPaintingData: NewPaintingData) {

    drawPaint.xfermode = null

    if (contextInstance.inputRecognizer.stylusDetected) {

        stylusPaintingData = modifiedNewPaintingData

    } else {

        fingerPaintingData = modifiedNewPaintingData

    }

}

fun PaintingCanvasView.changePaintingPathStrokeWidth(modifiedNewPaintingData: NewPaintingData) {

    if (contextInstance.inputRecognizer.stylusDetected) {

        stylusPaintingData = modifiedNewPaintingData

    } else {

        fingerPaintingData = modifiedNewPaintingData

    }

}

fun PaintingCanvasView.undoProcess() {

    if (allDrawingInformation.size > 0) {

        try {

            val itemToUndo = allDrawingInformation.removeAt(allDrawingInformation.size - 1)

            undoDrawingInformation.add(itemToUndo)

            overallRedrawPaintingDataRedo.add(overallRedrawPaintingData[overallRedrawPaintingData.size - 1])

            overallRedrawPaintingData.removeAt(overallRedrawPaintingData.size - 1)

        } catch (e: Exception) {
            e.printStackTrace()

        } finally {

            invalidate()

        }

    } else {

    }

}

fun PaintingCanvasView.redoProcess() {

    if (undoDrawingInformation.size > 0) {

        try {

            val itemToRedo = undoDrawingInformation.removeAt(undoDrawingInformation.size - 1)

            allDrawingInformation.add(itemToRedo)

            overallRedrawPaintingData.add(overallRedrawPaintingDataRedo[overallRedrawPaintingDataRedo.size - 1])

            overallRedrawPaintingDataRedo.removeAt(overallRedrawPaintingDataRedo.size - 1)

        } catch (e: Exception) {
            e.printStackTrace()

        } finally {

            invalidate()

        }

    } else {

    }

}

fun PaintingCanvasView.enableClearing() {

    drawPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

}

fun PaintingCanvasView.disableClearing() {

    drawPaint.xfermode = null

}

fun PaintingCanvasView.removeAllPaints() {

    allDrawingInformation.clear()

    overallRedrawPaintingData.clear()

    allRedrawPaintingPathData.clear()

    invalidate()

}

/* Redraw Process */
fun PaintingCanvasView.restorePaints() {

    redrawSavedPaints.runRestoreProcess(overallRedrawPaintingData).invokeOnCompletion {
        Log.d(this@restorePaints.javaClass.simpleName, "Redrawing Paints Completed")

    }

}

fun PaintingCanvasView.touchingStartRestore(x: Float, y: Float, pathColor: Int, pathStrokeWidth: Float) {

    undoDrawingInformation.clear()

    drawingPath.reset()
    drawingPath.moveTo(x, y)

    movingRedrawX = x
    movingRedrawY = y

    //Set New Color To Current Paint
    drawPaint.color = pathColor
    drawPaint.strokeWidth = pathStrokeWidth

    newPaintingData.paint?.let {
        drawPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
    }

    invalidate()

}

fun PaintingCanvasView.touchingMoveRestore(x: Float, y: Float) {

    val dX: Float = abs(x - movingRedrawX)
    val dY: Float = abs(y - movingRedrawY)

    if (dX >= touchTolerance || dY >= touchTolerance) {

        drawingPath.quadTo(movingRedrawX, movingRedrawY, (x + movingRedrawX) / 2, (y + movingRedrawY) / 2)

        movingRedrawX = x
        movingRedrawY = y

    }

    invalidate()

}

fun PaintingCanvasView.touchingUpRestore(pathColor: Int, pathStrokeWidth: Float) {

    drawingPath.lineTo(movingRedrawX, movingRedrawY)

    //Set New Color To New Paint
    val newPaintObject = Paint(drawPaint)
    newPaintObject.color = pathColor
    newPaintObject.strokeWidth = pathStrokeWidth

    newPaintingData.paint?.let {
        newPaintObject.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    allDrawingInformation.add(PaintingData(paint = newPaintObject, path = drawingPath))

    drawingPath = Path()

    invalidate()

}