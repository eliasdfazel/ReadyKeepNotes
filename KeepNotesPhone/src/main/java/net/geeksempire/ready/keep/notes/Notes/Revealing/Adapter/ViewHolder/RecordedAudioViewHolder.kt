/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Notes.Revealing.Adapter.ViewHolder

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.recorded_audio_items.view.*
import net.geeksempire.ProgressBar.HorizontalProgressView

class RecordedAudioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val rootItemView: ConstraintLayout = view.rootItemView
    val audioPlayPause: MaterialButton = view.audioPlayPause
    val audioProgressBar: HorizontalProgressView = view.audioProgressBar
    val audioDelete: MaterialButton = view.audioDelete
}