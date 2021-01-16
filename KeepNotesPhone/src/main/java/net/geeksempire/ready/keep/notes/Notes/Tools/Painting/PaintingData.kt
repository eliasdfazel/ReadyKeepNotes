package net.geeksempire.ready.keep.notes.Notes.Tools.Painting

import android.graphics.Paint
import android.graphics.Path
import androidx.annotation.Keep

@Keep
data class PaintingData(var paint: Paint, var path: Path, var paintColor: Int = 0x2325934, var paintStrokeWidth: Float = 3.0f)

@Keep
data class NewPaintingData(var paintColor: Int = 0x2325934, var paintStrokeWidth: Float = 3.0f, var paintStrokeSliderPosition: Float = 0f, var paint: Paint? = null)

@Keep
data class RedrawPaintingData(var xDrawPosition: Float, var yDrawPosition: Float, var paintColor: Int = 0x2325934, var paintStrokeWidth: Float = 3.0f)