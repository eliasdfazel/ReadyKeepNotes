package net.geeksempire.ready.keep.notes.Utils.Data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import java.io.ByteArrayOutputStream

fun Drawable.convertDrawableToByteArray() : ByteArray {

    val bitmap: Bitmap = (this@convertDrawableToByteArray as BitmapDrawable).bitmap

    val byteArrayOutputStream = ByteArrayOutputStream()

    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

    return byteArrayOutputStream.toByteArray()
}

fun Int.percentage(percentageAmount: Double) : Double {

    return (this@percentage * (percentageAmount)) / 100
}

fun Float.percentage(percentageAmount: Double) : Float {

    return ((this@percentage * (percentageAmount)) / 100).toFloat()
}

fun Float.convertToDeviceIndependentPixels(context: Context) : Int {

    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this@convertToDeviceIndependentPixels, context.resources.displayMetrics).toInt()
}