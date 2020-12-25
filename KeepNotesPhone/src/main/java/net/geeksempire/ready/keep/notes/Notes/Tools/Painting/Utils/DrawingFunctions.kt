package net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Utils

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.sin

fun drawSine(canvas: Canvas, paint: Paint) {

    val canvasWidth = canvas.width.toFloat()
    val canvasHeight = canvas.height.toFloat()

    canvas.drawLine(
        0f,
        canvasHeight / 2,
        canvasWidth,
        canvasHeight / 2,
        paint
    )

    var x = 0f

    while (x < canvasWidth) {

        val a = x / canvasWidth * (2.0.toFloat() * 3.131592654.toFloat())
        val y = canvasHeight / 2 - sin(a.toDouble()).toFloat() * (canvasHeight / 2.1.toFloat())

        canvas.drawPoint(x, y, paint)

        x++
    }

}