package net.geeksempire.keepnote.Notes.Painting

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

data class PaintingData(var paint: Paint, var path: Path, var paintColor: Int = Color.WHITE, var paintStrokeWidth: Float = 7.777f)

data class NewPaintingData(var paintColor: Int = Color.WHITE, var paintStrokeWidth: Float = 7.777f)
