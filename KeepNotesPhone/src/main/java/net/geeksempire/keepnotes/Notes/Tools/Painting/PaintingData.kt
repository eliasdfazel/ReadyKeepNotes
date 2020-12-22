package net.geeksempire.keepnotes.Notes.Tools.Painting

import android.graphics.Paint
import android.graphics.Path

data class PaintingData(var paint: Paint, var path: Path, var paintColor: Int = 0x2325934, var paintStrokeWidth: Float = 3.7531f)

data class NewPaintingData(var paintColor: Int = 0x2325934, var paintStrokeWidth: Float = 3.7531f, var paint: Paint? = null)

data class RedrawPaintingData(var xDrawPosition: Float, var yDrawPosition: Float, var paintColor: Int = 0x2325934, var paintStrokeWidth: Float = 3.7531f)