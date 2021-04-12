/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Adapter

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Extensions.changePaintingData
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.NewPaintingData
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.PaintingCanvasView
import net.geeksempire.ready.keep.notes.R

class RecentColorsAdapter (private val context: TakeNote, private val paintingCanvasView: PaintingCanvasView) : RecyclerView.Adapter<RecentColorsViewHolder>() {

    val allPickedColors: ArrayList<Int> = ArrayList<Int>()

    var allColorPalette: (() -> Unit)? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecentColorsViewHolder {

        return RecentColorsViewHolder(LayoutInflater.from(context).inflate(R.layout.colors_picked_item, viewGroup, false))
    }

    override fun onBindViewHolder(recentColorsViewHolder: RecentColorsViewHolder, position: Int) {

        recentColorsViewHolder.pickedColor.setImageDrawable(ColorDrawable(allPickedColors[position]))

        recentColorsViewHolder.pickedColor.setOnClickListener {

            paintingCanvasView.changePaintingData(NewPaintingData(paintColor = allPickedColors[position],
                paintStrokeWidth = if (context.inputRecognizer.stylusDetected) { paintingCanvasView.stylusPaintingData.paintStrokeWidth } else { paintingCanvasView.fingerPaintingData.paintStrokeWidth }))

            context.takeNoteLayoutBinding.colorPaletteInclude.colorPaletteView.color = allPickedColors[position]
            context.takeNoteLayoutBinding.colorPaletteInclude.pickColorView.iconTint = ColorStateList.valueOf(allPickedColors[position])

            allColorPalette?.invoke()

        }

    }

    override fun getItemCount(): Int {

        return allPickedColors.size
    }
}