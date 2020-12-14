package net.geeksempire.keepnote.Notes.Painting.Adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.geeksempire.keepnote.Notes.Painting.NewPaintingData
import net.geeksempire.keepnote.Notes.Painting.PaintingCanvasView
import net.geeksempire.keepnote.R

class RecentColorsAdapter (private val context: Context, private val paintingCanvasView: PaintingCanvasView) : RecyclerView.Adapter<RecentColorsViewHolder>() {

    val allPickedColors: ArrayList<Int> = ArrayList<Int>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecentColorsViewHolder {

        return RecentColorsViewHolder(LayoutInflater.from(context).inflate(R.layout.colors_picked_item, viewGroup, false))
    }

    override fun onBindViewHolder(recentColorsViewHolder: RecentColorsViewHolder, position: Int) {

        recentColorsViewHolder.pickedColor.backgroundTintList = ColorStateList.valueOf(allPickedColors[position])

        recentColorsViewHolder.pickedColor.setOnClickListener {

            paintingCanvasView.changePaintingData(NewPaintingData(paintColor = allPickedColors[position], paintStrokeWidth = paintingCanvasView.newPaintingData.paintStrokeWidth))

        }

    }

    override fun getItemCount(): Int {

        return allPickedColors.size
    }
}