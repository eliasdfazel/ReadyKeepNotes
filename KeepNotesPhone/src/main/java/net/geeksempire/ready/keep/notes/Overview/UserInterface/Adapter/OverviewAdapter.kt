package net.geeksempire.ready.keep.notes.Overview.UserInterface.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R

class OverviewAdapter(val context: KeepNoteOverview) : RecyclerView.Adapter<OverviewViewHolder>() {

    val notesDataStructureList: ArrayList<NotesDatabaseModel> = ArrayList<NotesDatabaseModel>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : OverviewViewHolder {

        return OverviewViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.overview_saved_notes_item, viewGroup, false))
    }

    override fun getItemCount() : Int {

        return notesDataStructureList.size
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)

        return notesDataStructureList[position].dataSelected
    }

    override fun onBindViewHolder(overviewViewHolder: OverviewViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(overviewViewHolder, position, payloads)

        when (context.themePreferences.checkThemeLightDark()) {
            ThemeType.ThemeLight -> {

                val tintedBackgroundDrawable =
                    context.getDrawable(R.drawable.round_corner_background)
                tintedBackgroundDrawable?.setTint(context.getColor(R.color.dark_transparent))

                overviewViewHolder.rootItemContentView.background = tintedBackgroundDrawable

                val imageContentBackground = context.getDrawable(R.drawable.round_corner_background)
                imageContentBackground?.setTint(context.getColor(R.color.dark_transparent))

                overviewViewHolder.contentImageView.background = imageContentBackground

                overviewViewHolder.titleTextView.setTextColor(context.getColor(R.color.dark))
                overviewViewHolder.contentTextView.setTextColor(context.getColor(R.color.dark))

            }
            ThemeType.ThemeDark -> {

                val tintedBackgroundDrawable =
                    context.getDrawable(R.drawable.round_corner_background)
                tintedBackgroundDrawable?.setTint(context.getColor(R.color.light_transparent))

                overviewViewHolder.rootItemContentView.background = tintedBackgroundDrawable

                val imageContentBackground = context.getDrawable(R.drawable.round_corner_background)
                imageContentBackground?.setTint(context.getColor(R.color.light_transparent))

                overviewViewHolder.contentImageView.background = imageContentBackground

                overviewViewHolder.titleTextView.setTextColor(context.getColor(R.color.light))
                overviewViewHolder.contentTextView.setTextColor(context.getColor(R.color.light))

            }
        }

    }

    override fun onBindViewHolder(overviewViewHolder: OverviewViewHolder, position: Int) {

        Firebase.auth.currentUser?.let {

            overviewViewHolder.titleTextView.text = context.contentEncryption.decryptEncodedData(
                notesDataStructureList[position].noteTile.toString(),
                it.uid
            )

            overviewViewHolder.contentTextView.text = context.contentEncryption.decryptEncodedData(
                notesDataStructureList[position].noteTextContent.toString(),
                it.uid
            )

        }

        when (context.themePreferences.checkThemeLightDark()) {
            ThemeType.ThemeLight -> {

                val tintedBackgroundDrawable =
                    context.getDrawable(R.drawable.round_corner_background)
                tintedBackgroundDrawable?.setTint(context.getColor(R.color.dark_transparent))

                overviewViewHolder.rootItemContentView.background = tintedBackgroundDrawable

                val imageContentBackground = context.getDrawable(R.drawable.round_corner_background)
                imageContentBackground?.setTint(context.getColor(R.color.dark_transparent))

                overviewViewHolder.contentImageView.background = imageContentBackground

                overviewViewHolder.titleTextView.setTextColor(context.getColor(R.color.dark))
                overviewViewHolder.contentTextView.setTextColor(context.getColor(R.color.dark))

            }
            ThemeType.ThemeDark -> {

                val tintedBackgroundDrawable =
                    context.getDrawable(R.drawable.round_corner_background)
                tintedBackgroundDrawable?.setTint(context.getColor(R.color.light_transparent))

                overviewViewHolder.rootItemContentView.background = tintedBackgroundDrawable

                val imageContentBackground = context.getDrawable(R.drawable.round_corner_background)
                imageContentBackground?.setTint(context.getColor(R.color.light_transparent))

                overviewViewHolder.contentImageView.background = imageContentBackground

                overviewViewHolder.titleTextView.setTextColor(context.getColor(R.color.light))
                overviewViewHolder.contentTextView.setTextColor(context.getColor(R.color.light))

            }
        }

        if (notesDataStructureList[position].noteHandwritingSnapshotLink == null) {

            overviewViewHolder.contentImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            overviewViewHolder.contentImageView.setColorFilter(context.getColor(R.color.pink_transparent))
            overviewViewHolder.contentImageView.setImageDrawable(context.getDrawable(R.drawable.icon_no_content))

        } else {

            overviewViewHolder.contentImageView.scaleType = ImageView.ScaleType.FIT_CENTER
            overviewViewHolder.contentImageView.clearColorFilter()

            Glide.with(context)
                .load(notesDataStructureList[position].noteHandwritingSnapshotLink)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(overviewViewHolder.contentImageView)

        }

        if (notesDataStructureList[position].noteVoiceContent == null) {

                overviewViewHolder.audioContentView.visibility = View.INVISIBLE

        } else {

            notesDataStructureList[position].noteVoiceContent?.let {

                if (it.isBlank()) {

                    overviewViewHolder.audioContentView.visibility = View.INVISIBLE

                } else {

                    overviewViewHolder.audioContentView.visibility = View.VISIBLE

                }

            }

        }

        overviewViewHolder.rootItemContentView.setOnClickListener {

            overviewViewHolder.waitingViewLoading.visibility = View.VISIBLE

            context.noteDatabaseConfigurations.updatedDatabaseItemPosition(position)
            context.noteDatabaseConfigurations.updatedDatabaseItemIdentifier(notesDataStructureList[position].uniqueNoteId)

            val paintingPathsJsonArray = notesDataStructureList[position].noteHandwritingPaintingPaths

            if (paintingPathsJsonArray.isNullOrEmpty()) {

                overviewViewHolder.waitingViewLoading.visibility = View.GONE

                TakeNote.open(context = context,
                    incomingActivityName = this@OverviewAdapter.javaClass.simpleName,
                    extraConfigurations = TakeNote.NoteConfigurations.KeyboardTyping,
                    uniqueNoteId = notesDataStructureList[position].uniqueNoteId,
                    noteTile = notesDataStructureList[position].noteTile,
                    contentText = notesDataStructureList[position].noteTextContent,
                    encryptedTextContent = true,
                    updateExistingNote = true
                )

            } else {

                overviewViewHolder.waitingViewLoading.visibility = View.GONE

                TakeNote.open(context = context,
                    incomingActivityName = this@OverviewAdapter.javaClass.simpleName,
                    extraConfigurations = TakeNote.NoteConfigurations.KeyboardTyping,
                    uniqueNoteId = notesDataStructureList[position].uniqueNoteId,
                    noteTile = notesDataStructureList[position].noteTile,
                    contentText = notesDataStructureList[position].noteTextContent,
                    paintingPath = paintingPathsJsonArray,
                    encryptedTextContent = true,
                    updateExistingNote = true
                )

            }

        }

    }

}