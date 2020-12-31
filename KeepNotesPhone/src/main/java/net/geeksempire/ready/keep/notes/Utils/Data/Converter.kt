package net.geeksempire.ready.keep.notes.Utils.Data

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.ByteArrayOutputStream

fun Drawable.convertDrawableToByteArray() : ByteArray {

    val bitmap: Bitmap = (this@convertDrawableToByteArray as BitmapDrawable).bitmap

    val byteArrayOutputStream = ByteArrayOutputStream()

    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

    return byteArrayOutputStream.toByteArray()
}