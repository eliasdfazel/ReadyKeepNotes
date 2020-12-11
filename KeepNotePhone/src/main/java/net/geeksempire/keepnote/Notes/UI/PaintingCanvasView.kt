package net.geeksempire.keepnote.Notes.UI

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class PaintingCanvasView(context: Context?) : View(context), View.OnTouchListener {

    private val canvas: Canvas = Canvas()
    private var drawPaint: Paint = Paint()

    private var path: Path = Path()

    private var movingX: Float = 0f
    private  var movingY: Float = 0f

    private var touchTolerance: Float = 4f

    private val drawingPaths = ArrayList<Path>()
    private val undoDrawingPaths = ArrayList<Path>()

    init {

        isFocusable = true
        isFocusableInTouchMode = true

        setOnTouchListener(this@PaintingCanvasView)

    }

    fun setupPaintingPanel(paintColor: Int = Color.WHITE, paintStrokeWidth: Float = 5.0f) {

        drawPaint.color = paintColor
        drawPaint.strokeWidth = paintStrokeWidth

        drawPaint.isAntiAlias = true
        drawPaint.isDither = true

        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.MITER
        drawPaint.strokeCap = Paint.Cap.ROUND

    }

    fun changePaintingColor() {

        drawPaint.color = Color.RED

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let {

            for (aPath in drawingPaths) {
                canvas.drawPath(aPath, drawPaint)
            }

            canvas.drawPath(path, drawPaint)

        }

    }

    private fun touchingStart(x: Float, y: Float) {

        undoDrawingPaths.clear()

        path.reset()
        path.moveTo(x, y)

        movingX = x
        movingY = y
    }

    private fun touchingMove(x: Float, y: Float) {

        val dX: Float = abs(x - movingX)
        val dY: Float = abs(y - movingY)

        if (dX >= touchTolerance || dY >= touchTolerance) {

            path.quadTo(movingX, movingY, (x + movingX) / 2, (y + movingY) / 2)

            movingX = x
            movingY = y

        }

    }

    private fun touchingUp() {

        path.lineTo(movingX, movingY)

        canvas.drawPath(path, drawPaint)

        drawingPaths.add(path)

        path = Path()

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

        if (drawingPaths.size > 0) {

            undoDrawingPaths.add(drawingPaths.removeAt(drawingPaths.size - 1))

            invalidate()

        } else {

        }

    }

    fun redoProcess() {

        if (undoDrawingPaths.size > 0) {

            drawingPaths.add(undoDrawingPaths.removeAt(undoDrawingPaths.size - 1))

            invalidate()

        } else {

        }

    }

    fun removeAllPaints() {


    }

}