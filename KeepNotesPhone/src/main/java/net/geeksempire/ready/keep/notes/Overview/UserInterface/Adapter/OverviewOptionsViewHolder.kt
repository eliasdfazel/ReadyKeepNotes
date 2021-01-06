package net.geeksempire.ready.keep.notes.Overview.UserInterface.Adapter

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.overview_options_item.view.*
import net.geekstools.imageview.customshapes.ShapesImage

class OverviewOptionsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val rootItemOptionsView: ConstraintLayout = view.rootItemView
    val closeOptionsMenu: ShapesImage = view.closeOptionsMenu
}