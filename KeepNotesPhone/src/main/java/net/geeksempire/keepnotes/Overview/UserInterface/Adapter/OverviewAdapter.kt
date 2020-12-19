package net.geeksempire.keepnotes.Overview.UserInterface.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.DocumentSnapshot
import net.geeksempire.keepnotes.Database.DataStructure.Notes
import net.geeksempire.keepnotes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.keepnotes.Preferences.Theme.ThemeType
import net.geeksempire.keepnotes.R

class OverviewAdapter (private val context: KeepNoteOverview) : RecyclerView.Adapter<OverviewViewHolder>() {

    val notesDataStructureList: ArrayList<DocumentSnapshot> = ArrayList<DocumentSnapshot>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): OverviewViewHolder {

        return OverviewViewHolder(LayoutInflater.from(context).inflate(R.layout.overview_saved_notes_item, viewGroup, false))
    }

    override fun onBindViewHolder(overviewViewHolder: OverviewViewHolder, position: Int) {

        context.firebaseUser?.let {

            overviewViewHolder.titleTextView.text = context.contentEncryption.decryptEncodedData(notesDataStructureList[position][Notes.NoteTile].toString(), context.firebaseUser.uid)

            overviewViewHolder.contentTextView.text = context.contentEncryption.decryptEncodedData(notesDataStructureList[position][Notes.NoteTextContent].toString(), context.firebaseUser.uid)

        }

        when (context.themePreferences.checkLightDark()) {
            ThemeType.Light -> {

                val tintedBackgroundDrawable = context.getDrawable(R.drawable.round_corner_background)
                tintedBackgroundDrawable?.setTint(context.getColor(R.color.dark_transparent_high))

                overviewViewHolder.rootItemView.background = tintedBackgroundDrawable

                val imageContentBackground = context.getDrawable(R.drawable.round_corner_background)
                imageContentBackground?.setTint(context.getColor(R.color.dark_transparent_high))

                overviewViewHolder.contentImageView.background = imageContentBackground

                overviewViewHolder.titleTextView.setTextColor(context.getColor(R.color.dark))
                overviewViewHolder.contentTextView.setTextColor(context.getColor(R.color.dark))

            }
            ThemeType.Dark -> {

                val tintedBackgroundDrawable = context.getDrawable(R.drawable.round_corner_background)
                tintedBackgroundDrawable?.setTint(context.getColor(R.color.light_transparent_high))

                overviewViewHolder.rootItemView.background = tintedBackgroundDrawable

                val imageContentBackground = context.getDrawable(R.drawable.round_corner_background)
                imageContentBackground?.setTint(context.getColor(R.color.light_transparent_high))

                overviewViewHolder.contentImageView.background = imageContentBackground

                overviewViewHolder.titleTextView.setTextColor(context.getColor(R.color.light))
                overviewViewHolder.contentTextView.setTextColor(context.getColor(R.color.light))

            }
        }

        Glide.with(context)
            .load(notesDataStructureList[position][Notes.NoteHandwritingSnapshotLink].toString())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(overviewViewHolder.contentImageView)

    }

    override fun getItemCount(): Int {

        return notesDataStructureList.size
    }

}