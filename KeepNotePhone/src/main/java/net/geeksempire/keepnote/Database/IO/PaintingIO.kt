package net.geeksempire.keepnote.Database.IO

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import java.io.ByteArrayOutputStream

class PaintingIO() {

    fun takeScreenshot(viewToTakeSnapshot: View) : ByteArray {

        val byteArrayOutputStream = ByteArrayOutputStream()

        screenshotBitmap(viewToTakeSnapshot).compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

        return byteArrayOutputStream.toByteArray()
    }

    private fun screenshotBitmap(view: View) : Bitmap {

        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return bitmap

    }

}