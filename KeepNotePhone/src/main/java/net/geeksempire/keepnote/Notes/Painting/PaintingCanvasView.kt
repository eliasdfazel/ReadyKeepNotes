package net.geeksempire.keepnote.Notes.Painting

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class PaintingCanvasView(context: Context?) : View(context), View.OnTouchListener {

    private var readyCanvas: Canvas? = null

    private var drawPaint: Paint = Paint()

    private var drawingPath: Path = Path()

    private var movingX: Float = 0f
    private  var movingY: Float = 0f

    private var touchTolerance: Float = 4f

    private val allDrawingInformation = ArrayList<PaintingData>()

    private val undoDrawingInformation = ArrayList<PaintingData>()

    var newPaintingData: NewPaintingData = NewPaintingData()

    init {

        this@PaintingCanvasView.isFocusable = true
        this@PaintingCanvasView.isFocusableInTouchMode = true

        this@PaintingCanvasView.setOnTouchListener(this@PaintingCanvasView)

    }

    fun setupPaintingPanel(paintColor: Int = Color.WHITE, paintStrokeWidth: Float = 7.777f) {

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

                    invalidate()

                }
                MotionEvent.ACTION_MOVE -> {

                    touchingMove(initialTouchX, initialTouchY)

                    invalidate()
                }
                MotionEvent.ACTION_UP -> {

                    touchingUp()

                    invalidate()

                }
            }

        }

        return true
    }

    private fun touchingStart(x: Float, y: Float) {

        undoDrawingInformation.clear()

        drawingPath.reset()
        drawingPath.moveTo(x, y)

        movingX = x
        movingY = y

        //Set New Color To Current Paint
        drawPaint.color = newPaintingData.paintColor

        newPaintingData.paint?.let {
            drawPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
        }

    }

    private fun touchingMove(x: Float, y: Float) {

        val dX: Float = abs(x - movingX)
        val dY: Float = abs(y - movingY)

        if (dX >= touchTolerance || dY >= touchTolerance) {

            drawingPath.quadTo(movingX, movingY, (x + movingX) / 2, (y + movingY) / 2)

            movingX = x
            movingY = y

        }

    }

    private fun touchingUp() {

        drawingPath.lineTo(movingX, movingY)

        //Set New Color To New Paint
        val newPaintObject = Paint(drawPaint)
        newPaintObject.color = newPaintingData.paintColor

        newPaintingData.paint?.let {
            newPaintObject.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        allDrawingInformation.add(PaintingData(paint = newPaintObject, path = drawingPath))

        drawingPath = Path()

    }

    fun changePaintingData(modifiedNewPaintingData: NewPaintingData) {

        drawPaint.xfermode = null

        newPaintingData = modifiedNewPaintingData

    }

    fun undoProcess() {

        if (allDrawingInformation.size > 0) {

            undoDrawingInformation.add(allDrawingInformation.removeAt(allDrawingInformation.size - 1))

            invalidate()

        } else {

        }

    }

    fun redoProcess() {

        if (undoDrawingInformation.size > 0) {

            allDrawingInformation.add(undoDrawingInformation.removeAt(undoDrawingInformation.size - 1))

            invalidate()

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

}