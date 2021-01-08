package net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface.Adapter

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.overview_saved_notes_item.view.*
import net.geeksempire.loadingspin.SpinKitView
import net.geekstools.imageview.customshapes.ShapesImage

class SearchResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val rootItemView: ConstraintLayout = view.rootItemView
    val rootItemContentView: ConstraintLayout = view.rootItemView
    val titleTextView: TextView = view.titleTextView
    val contentTextView: TextView = view.contentTextView
    val contentImageView: ShapesImage = view.contentImageView
    val waitingViewLoading: SpinKitView = view.waitingViewLoading
}