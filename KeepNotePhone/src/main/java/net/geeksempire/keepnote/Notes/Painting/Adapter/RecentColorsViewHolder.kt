package net.geeksempire.keepnote.Notes.Painting.Adapter

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.colors_picked_item.view.*

class RecentColorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val itemRootView: ConstraintLayout = itemView.itemRootView
    val pickedColor: MaterialButton = itemView.pickedColor
}