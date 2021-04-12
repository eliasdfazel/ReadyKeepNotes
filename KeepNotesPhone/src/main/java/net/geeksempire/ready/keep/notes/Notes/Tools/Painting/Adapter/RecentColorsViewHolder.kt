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

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.colors_picked_item.view.*
import net.geekstools.imageview.customshapes.ShapesImage

class RecentColorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val itemRootView: ConstraintLayout = itemView.itemRootView
    val pickedColor: ShapesImage = itemView.pickedColor
}