package net.geeksempire.keepnotes.Notes.Tools.Painting

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geeksempire.keepnotes.Notes.Restoring.RedrawSavedPaints
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class PaintingCanvasView(context: Context) : View(context), View.OnTouchListener {

    private var readyCanvas: Canvas? = null

    private var drawPaint: Paint = Paint()

    private var drawingPath: Path = Path()

    private var movingX: Float = 0f
    private  var movingY: Float = 0f

    private var movingRedrawX: Float = 0f
    private  var movingRedrawY: Float = 0f

    private var touchTolerance: Float = 4f

    private val allDrawingInformation = ArrayList<PaintingData>()

    private val undoDrawingInformation = ArrayList<PaintingData>()

    var newPaintingData: NewPaintingData = NewPaintingData()

    val redrawSavedPaints: RedrawSavedPaints = RedrawSavedPaints(this@PaintingCanvasView)

    val allRedrawPaintingData: ArrayList<ArrayList<RedrawPaintingData>> = ArrayList<ArrayList<RedrawPaintingData>>()

    lateinit var allRedrawPaintingPathData: ArrayList<RedrawPaintingData>

    init {

        this@PaintingCanvasView.isFocusable = true
        this@PaintingCanvasView.isFocusableInTouchMode = true

        this@PaintingCanvasView.setOnTouchListener(this@PaintingCanvasView)

    }

    fun setupPaintingPanel(paintColor: Int = Color.BLUE, paintStrokeWidth: Float = 3.7531f) = CoroutineScope(Dispatchers.Main).launch {

        drawPaint.color = paintColor
        drawPaint.strokeWidth = paintStrokeWidth

        drawPaint.isAntiAlias = true
        drawPaint.isDither = true

        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.MITER
        drawPaint.strokeCap = Paint.Cap.ROUND

        newPaintingData = NewPaintingData(paintColor, paintStrokeWidth)

    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let {

            readyCanvas = canvas

            allDrawingInformation.forEachIndexed { index, paintingPathData ->


                canvas.drawPath(paintingPathData.path, paintingPathData.paint)

            }

            canvas.drawPath(drawingPath, drawPaint)

        }

    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {

        motionEvent?.let {

            val initialTouchX = motionEvent.x
            val initialTouchY = motionEvent.y

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {

                    touchingStart(initialTouchX, initialTouchY)

                }
                MotionEvent.ACTION_MOVE -> {

                    touchingMove(initialTouchX, initialTouchY)

                }
                MotionEvent.ACTION_UP -> {

                    touchingUp()

                }
            }

        }

        return true
    }

    private fun touchingStart(x: Float, y: Float) {

        undoDrawingInformation.clear()

        drawingPath.reset()
        drawingPath.moveTo(x, y)

        allRedrawPaintingPathData =  ArrayList<RedrawPaintingData>()
        allRedrawPaintingPathData.clear()

        allRedrawPaintingPathData.add(0, RedrawPaintingData(x, y))
        allRedrawPaintingPathData.add(RedrawPaintingData(x, y))

        movingX = x
        movingY = y

        //Set New Color To Current Paint
        drawPaint.color = newPaintingData.paintColor

        newPaintingData.paint?.let {
            drawPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
        }

        invalidate()

    }

    private fun touchingMove(x: Float, y: Float) {

        allRedrawPaintingPathData.add(RedrawPaintingData(x, y))

        val dX: Float = abs(x - movingX)
        val dY: Float = abs(y - movingY)

        if (dX >= touchTolerance || dY >= touchTolerance) {

            drawingPath.quadTo(movingX, movingY, (x + movingX) / 2, (y + movingY) / 2)

            movingX = x
            movingY = y

        }

        invalidate()

    }

    private fun touchingUp() {

        allRedrawPaintingPathData.add(RedrawPaintingData(movingX, movingY))

        allRedrawPaintingData.add(allRedrawPaintingPathData)

        drawingPath.lineTo(movingX, movingY)

        //Set New Color To New Paint
        val newPaintObject = Paint(drawPaint)
        newPaintObject.color = newPaintingData.paintColor

        newPaintingData.paint?.let {
            newPaintObject.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        allDrawingInformation.add(PaintingData(paint = newPaintObject, path = drawingPath))

        drawingPath = Path()

        invalidate()

    }

    fun changePaintingData(modifiedNewPaintingData: NewPaintingData) {

        drawPaint.xfermode = null

        newPaintingData = modifiedNewPaintingData

    }

    fun undoProcess() {

        if (allDrawingInformation.size > 0) {

            try {

                undoDrawingInformation.add(allDrawingInformation.removeAt(allDrawingInformation.size - 1))

            } catch (e: Exception) {
                e.printStackTrace()

            } finally {

                invalidate()

            }

        } else {

        }

    }

    fun redoProcess() {

        if (undoDrawingInformation.size > 0) {

            try {

                allDrawingInformation.add(undoDrawingInformation.removeAt(undoDrawingInformation.size - 1))

            } catch (e: Exception) {
                e.printStackTrace()

            } finally {

                invalidate()

            }

        } else {

        }

    }

    fun enableClearing() {

        drawPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

    }

    fun disableClearing() {

        drawPaint.xfermode = null

    }

    fun removeAllPaints() {

        allDrawingInformation.clear()

        invalidate()

    }

    fun restorePaints() {

        redrawSavedPaints.runRestoreProcess(allRedrawPaintingData).invokeOnCompletion {
            Log.d(this@PaintingCanvasView.javaClass.simpleName, "Redrawing Paints Completed")

        }

    }

    fun touchingStartRestore(x: Float, y: Float) {

        undoDrawingInformation.clear()

        drawingPath.reset()
        drawingPath.moveTo(x, y)

        movingRedrawX = x
        movingRedrawY = y

        //Set New Color To Current Paint
        drawPaint.color = newPaintingData.paintColor

        newPaintingData.paint?.let {
            drawPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
        }

        invalidate()

    }

    fun touchingMoveRestore(x: Float, y: Float) {

        val dX: Float = abs(x - movingRedrawX)
        val dY: Float = abs(y - movingRedrawY)

        if (dX >= touchTolerance || dY >= touchTolerance) {

            drawingPath.quadTo(movingRedrawX, movingRedrawY, (x + movingRedrawX) / 2, (y + movingRedrawY) / 2)

            movingRedrawX = x
            movingRedrawY = y

        }

        invalidate()

    }

    fun touchingUpRestore() {

        drawingPath.lineTo(movingRedrawX, movingRedrawY)

        //Set New Color To New Paint
        val newPaintObject = Paint(drawPaint)
        newPaintObject.color = newPaintingData.paintColor

        newPaintingData.paint?.let {
            newPaintObject.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        allDrawingInformation.add(PaintingData(paint = newPaintObject, path = drawingPath))

        drawingPath = Path()

        invalidate()

    }
}