package net.geeksempire.keepnote.Notes.Painting

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class PaintingCanvasView(context: Context?) : View(context), View.OnTouchListener {

    private var drawPaint: Paint = Paint()

    private var drawingPath: Path = Path()

    private var movingX: Float = 0f
    private  var movingY: Float = 0f

    private var touchTolerance: Float = 4f

    private var changedColor: Int = Color.WHITE

    private val allDrawingInformation = ArrayList<PaintingData>()

    private val undoDrawingInformation = ArrayList<PaintingData>()

    init {

        this@PaintingCanvasView.isFocusable = true
        this@PaintingCanvasView.isFocusableInTouchMode = true

        this@PaintingCanvasView.setOnTouchListener(this@PaintingCanvasView)

    }

    fun setupPaintingPanel(paintColor: Int = Color.WHITE, paintStrokeWidth: Float = 5.0f) {

        changedColor = paintColor

        drawPaint.color = paintColor
        drawPaint.strokeWidth = paintStrokeWidth

        drawPaint.isAntiAlias = true
        drawPaint.isDither = true

        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.MITER
        drawPaint.strokeCap = Paint.Cap.ROUND

    }

    fun changePaintingColor(newColor: Int) {

        changedColor = newColor

    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let {

            allDrawingInformation.forEachIndexed { index, paintingPathData ->

                canvas.drawPath(paintingPathData.path, paintingPathData.paint)

            }

            canvas.drawPath(drawingPath, drawPaint)

        }

    }

    private fun touchingStart(x: Float, y: Float) {

        undoDrawingInformation.clear()

        drawingPath.reset()
        drawingPath.moveTo(x, y)

        movingX = x
        movingY = y

        //Set New Color To Current Paint
        drawPaint.color = changedColor


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
        newPaintObject.color = changedColor

        allDrawingInformation.add(PaintingData(paint = newPaintObject, path = drawingPath))

        drawingPath = Path()

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

    fun removeAllPaints() {


    }

}