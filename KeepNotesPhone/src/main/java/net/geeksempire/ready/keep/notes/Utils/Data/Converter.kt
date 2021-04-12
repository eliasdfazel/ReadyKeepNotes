/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Utils.Data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
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

    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this@convertToDeviceIndependentPixels,
        context.resources.displayMetrics
    ).toInt()
}

fun Drawable.resizeDrawable(context: Context, dstWidth: Int, dstHeight: Int): Drawable? {

    var resizedDrawable: Drawable? = null

    resizedDrawable = try {

        val bitmap = (this@resizeDrawable as BitmapDrawable).bitmap
        val bitmapResized = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false)

        BitmapDrawable(context.resources, bitmapResized).mutate()

    } catch (e: Exception) {
        e.printStackTrace()

        ColorDrawable(Color.TRANSPARENT)

    }

    return resizedDrawable
}