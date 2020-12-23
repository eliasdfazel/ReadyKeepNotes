package net.geeksempire.keepnotes.Database.IO

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.*
import net.geeksempire.keepnotes.Notes.Tools.Painting.PaintingCanvasView
import net.geeksempire.keepnotes.Notes.Tools.Painting.RedrawPaintingData
import net.geeksempire.keepnotes.Utils.PreferencesIO.SavePreferences
import org.json.JSONArray
import java.io.ByteArrayOutputStream

class PaintingIO (private val context: Context) {

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

    fun saveRecentPickedColor(pickedColor: Int) {

        val colorsSharedPreferences = context.getSharedPreferences("RecentPickedColors", Context.MODE_PRIVATE)

        val savePreferences = SavePreferences(context)
        savePreferences.savePreference("RecentPickedColors", colorsSharedPreferences.all.size.toString(), pickedColor)

        if (colorsSharedPreferences.all.size > 19) {

            removeOldPickedColors()

        }

    }

    fun readRecentPickedColor() : ArrayList<Int> {

        val recentPickedColors = ArrayList<Int>()

        val recentColorsValue = context.getSharedPreferences("RecentPickedColors", Context.MODE_PRIVATE).all

        if (recentColorsValue.isNotEmpty()) {

            recentColorsValue.values.forEach {

                if (it == null) {

                    recentPickedColors.add(Color.WHITE)

                } else {

                    recentPickedColors.add(it.toString().toInt())

                }

            }

        } else {

            recentPickedColors.addAll(arrayListOf(Color.WHITE, Color.WHITE, Color.WHITE))

        }

        return recentPickedColors
    }

    fun removeOldPickedColors() = CoroutineScope(Dispatchers.IO).async {

        val colorsSharedPreferences = context.getSharedPreferences("RecentPickedColors", Context.MODE_PRIVATE)

        for (colorIndex in 0..7) {

            colorsSharedPreferences.edit().remove(colorIndex.toString()).apply()

        }

        colorsSharedPreferences.all.entries.forEachIndexed { index, entry ->

            colorsSharedPreferences.edit().putInt(index.toString(), entry.value.toString().toInt())

        }

    }

    fun preparePaintingPaths(paintingCanvasView: PaintingCanvasView, paintingPathsJsonArray: ArrayList<DocumentSnapshot>) = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

        paintingPathsJsonArray.forEach { paintingPaths ->

            val jsonArrayPaths = JSONArray(paintingPaths["paintPath"].toString())

            paintingCanvasView.allRedrawPaintingPathData =  ArrayList<RedrawPaintingData>()

            for (pathIndex in 0 until jsonArrayPaths.length()) {

                val xDrawPosition = jsonArrayPaths.getJSONObject(pathIndex).get("xDrawPosition").toString().toFloat()
                val yDrawPosition = jsonArrayPaths.getJSONObject(pathIndex).get("yDrawPosition").toString().toFloat()

                val paintColor = jsonArrayPaths.getJSONObject(pathIndex).get("paintColor").toString().toInt()
                val paintStrokeWidth = jsonArrayPaths.getJSONObject(pathIndex).get("paintStrokeWidth").toString().toFloat()

                paintingCanvasView.allRedrawPaintingPathData.add(RedrawPaintingData(
                    xDrawPosition = xDrawPosition,
                    yDrawPosition = yDrawPosition,
                    paintColor = paintColor,
                    paintStrokeWidth = paintStrokeWidth
                ))

            }

            paintingCanvasView.allRedrawPaintingData.add(paintingCanvasView.allRedrawPaintingPathData)

        }

    }

}