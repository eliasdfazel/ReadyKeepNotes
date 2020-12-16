package net.geeksempire.keepnotes.Notes.Painting.Adapter

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.geeksempire.keepnotes.Notes.Painting.NewPaintingData
import net.geeksempire.keepnotes.Notes.Painting.PaintingCanvasView
import net.geeksempire.keepnotes.Notes.Taking.TakeNote
import net.geeksempire.keepnotes.R

class RecentColorsAdapter (private val context: TakeNote, private val paintingCanvasView: PaintingCanvasView) : RecyclerView.Adapter<RecentColorsViewHolder>() {

    val allPickedColors: ArrayList<Int> = ArrayList<Int>()

    var allColorPalette: (() -> Unit)? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecentColorsViewHolder {

        return RecentColorsViewHolder(LayoutInflater.from(context).inflate(R.layout.colors_picked_item, viewGroup, false))
    }

    override fun onBindViewHolder(recentColorsViewHolder: RecentColorsViewHolder, position: Int) {

        recentColorsViewHolder.pickedColor.setImageDrawable(ColorDrawable(allPickedColors[position]))

        recentColorsViewHolder.pickedColor.setOnClickListener {

            paintingCanvasView.changePaintingData(NewPaintingData(paintColor = allPickedColors[position], paintStrokeWidth = paintingCanvasView.newPaintingData.paintStrokeWidth))

            context.takeNoteLayoutBinding.colorPaletteInclude.colorPaletteView.color = allPickedColors[position]
            context.takeNoteLayoutBinding.colorPaletteInclude.pickColorView.iconTint = ColorStateList.valueOf(allPickedColors[position])

            allColorPalette?.invoke()

        }

    }

    override fun getItemCount(): Int {

        return allPickedColors.size
    }
}