package net.geeksempire.keepnotes.Notes.Tools.Painting.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.*
import net.geeksempire.keepnotes.Notes.Tools.Painting.NewPaintingData
import net.geeksempire.keepnotes.Notes.Tools.Painting.PaintingData
import net.geeksempire.keepnotes.Notes.Tools.Painting.RedrawPaintingData
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class StrokePaintingCanvasView(context: Context) : View(context), View.OnTouchListener {

    private var readyCanvas: Canvas? = null

    private var drawPaint: Paint = Paint()

    private var drawingPath: Path = Path()

    private var movingX: Float = 0f
    private var movingY: Float = 0f

    private var movingRedrawX: Float = 0f
    private var movingRedrawY: Float = 0f

    private var touchTolerance: Float = 4f

    private val allDrawingInformation = ArrayList<PaintingData>()

    private val undoDrawingInformation = ArrayList<PaintingData>()

    val allRedrawPaintingData: ArrayList<ArrayList<RedrawPaintingData>> =
        ArrayList<ArrayList<RedrawPaintingData>>()

    lateinit var allRedrawPaintingPathData: ArrayList<RedrawPaintingData>

    var newPaintingData: NewPaintingData = NewPaintingData()

    init {

        this@StrokePaintingCanvasView.isFocusable = true
        this@StrokePaintingCanvasView.isFocusableInTouchMode = true

        this@StrokePaintingCanvasView.setOnTouchListener(this@StrokePaintingCanvasView)

    }

    fun setupPaintingPanel(paintColor: Int = Color.BLUE, paintStrokeWidth: Float = 3.0f) =
        CoroutineScope(Dispatchers.Main).launch {

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

        removeAllPaints()

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

        allRedrawPaintingPathData.add(
            0, RedrawPaintingData(
                x,
                y,
                newPaintingData.paintColor,
                newPaintingData.paintStrokeWidth
            )
        )
        allRedrawPaintingPathData.add(
            RedrawPaintingData(
                x,
                y,
                newPaintingData.paintColor,
                newPaintingData.paintStrokeWidth
            )
        )

        invalidate()

    }

    private fun touchingMove(x: Float, y: Float) {

        allRedrawPaintingPathData.add(
            RedrawPaintingData(
                x,
                y,
                newPaintingData.paintColor,
                newPaintingData.paintStrokeWidth
            )
        )

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

        allRedrawPaintingPathData.add(
            RedrawPaintingData(
                movingX,
                movingY,
                newPaintingData.paintColor,
                newPaintingData.paintStrokeWidth
            )
        )

        allRedrawPaintingData.add(allRedrawPaintingPathData)

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

    fun changePaintingData(modifiedNewPaintingData: NewPaintingData) {

        drawPaint.xfermode = null

        newPaintingData = modifiedNewPaintingData

    }

    fun changePaintingPathStrokeWidth(modifiedNewPaintingData: NewPaintingData) {

        newPaintingData = modifiedNewPaintingData

    }

    fun removeAllPaints() {

        allDrawingInformation.clear()

        invalidate()

    }

    fun runRestoreProcess(allRedrawPaintingData: ArrayList<ArrayList<RedrawPaintingData>>) =
        CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {

            if (allRedrawPaintingData.isNotEmpty()) {

                startPainting(allRedrawPaintingData.last())

            }

        }

    private fun startPainting(allRedrawPaintingPathData: ArrayList<RedrawPaintingData>) =
        CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
            Log.d(
                this@StrokePaintingCanvasView.javaClass.simpleName,
                "${allRedrawPaintingPathData[0].xDrawPosition} | ${allRedrawPaintingPathData[0].yDrawPosition}"
            )

            delay(1133)

            touchingStartRestore(
                allRedrawPaintingPathData[0].xDrawPosition,
                allRedrawPaintingPathData[0].yDrawPosition,
                allRedrawPaintingPathData[0].paintColor,
                newPaintingData.paintStrokeWidth
            )

            touchingMoveRestore(
                allRedrawPaintingPathData[0].xDrawPosition,
                allRedrawPaintingPathData[0].yDrawPosition
            )

            allRedrawPaintingPathData.forEachIndexed paintingLoop@{ index, redrawPaintingData ->

                touchingMoveRestore(
                    redrawPaintingData.xDrawPosition,
                    redrawPaintingData.yDrawPosition
                )

            }

            touchingUpRestore(
                allRedrawPaintingPathData[0].paintColor,
                newPaintingData.paintStrokeWidth
            )

        }

    private fun touchingStartRestore(x: Float, y: Float, pathColor: Int, pathStrokeWidth: Float) {

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

    private fun touchingMoveRestore(x: Float, y: Float) {

        val dX: Float = abs(x - movingRedrawX)
        val dY: Float = abs(y - movingRedrawY)

        if (dX >= touchTolerance || dY >= touchTolerance) {

            drawingPath.quadTo(
                movingRedrawX,
                movingRedrawY,
                (x + movingRedrawX) / 2,
                (y + movingRedrawY) / 2
            )

            movingRedrawX = x
            movingRedrawY = y

        }

        invalidate()

    }

    private fun touchingUpRestore(pathColor: Int, pathStrokeWidth: Float) {

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

}