package net.geeksempire.ready.keep.notes.Notes.Revealing.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.geeksempire.ready.keep.notes.Notes.Revealing.Adapter.ViewHolder.RecordedAudioViewHolder
import net.geeksempire.ready.keep.notes.Notes.Revealing.DataStructure.RecordedAudioDataStructure
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemePreferences
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R

class RecordedAudioAdapter(private val context: Context) : RecyclerView.Adapter<RecordedAudioViewHolder>() {

    val themePreferences: ThemePreferences by lazy {
        ThemePreferences(context)
    }

    val notesDataStructureList: ArrayList<RecordedAudioDataStructure> = ArrayList<RecordedAudioDataStructure>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : RecordedAudioViewHolder {

        return RecordedAudioViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.overview_pinned_notes_item, viewGroup, false)
        )
    }

    override fun getItemCount() : Int {

        return notesDataStructureList.size
    }

    override fun onBindViewHolder(
        recordedAudioViewHolder: RecordedAudioViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(recordedAudioViewHolder, position, payloads)

        when (themePreferences.checkThemeLightDark()) {
            ThemeType.ThemeLight -> {



            }
            ThemeType.ThemeDark -> {



            }
        }

    }

    override fun onBindViewHolder(RecordedAudioViewHolder: RecordedAudioViewHolder, position: Int) {



    }

}