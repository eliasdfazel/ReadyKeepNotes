package net.geeksempire.ready.keep.notes.Notes.Revealing.Adapter.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recorded_audio_items.view.*

class RecordedAudioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val rootItemView = view.rootItemView
    val audioPlayPause = view.audioPlayPause
    val audioProgressBar = view.audioProgressBar
}