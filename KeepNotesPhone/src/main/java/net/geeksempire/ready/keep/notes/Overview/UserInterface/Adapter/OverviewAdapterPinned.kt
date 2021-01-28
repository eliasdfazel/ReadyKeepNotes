package net.geeksempire.ready.keep.notes.Overview.UserInterface.Adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Overview.UserInterface.Adapter.ViewHolders.OverviewPinnedViewHolder
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R

class OverviewAdapterPinned(val context: KeepNoteOverview) : RecyclerView.Adapter<OverviewPinnedViewHolder>() {

    val notesDataStructureList: ArrayList<NotesDatabaseModel> = ArrayList<NotesDatabaseModel>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : OverviewPinnedViewHolder {

        return OverviewPinnedViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.overview_pinned_notes_item, viewGroup, false))
    }

    override fun getItemCount() : Int {

        return notesDataStructureList.size
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)

        return notesDataStructureList[position].dataSelected
    }

    override fun onBindViewHolder(overviewPinnedViewHolder: OverviewPinnedViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(overviewPinnedViewHolder, position, payloads)

        when (context.themePreferences.checkThemeLightDark()) {
            ThemeType.ThemeLight -> {

                val tintedBackgroundDrawable =
                    context.getDrawable(R.drawable.round_corner_background)
                tintedBackgroundDrawable?.setTint(context.getColor(R.color.dark_transparent))

                overviewPinnedViewHolder.rootItemContentView.background = tintedBackgroundDrawable

                val imageContentBackground = context.getDrawable(R.drawable.round_corner_background)
                imageContentBackground?.setTint(context.getColor(R.color.dark_transparent))

                overviewPinnedViewHolder.contentImageView.background = imageContentBackground

                overviewPinnedViewHolder.titleTextView.setTextColor(context.getColor(R.color.dark))
                overviewPinnedViewHolder.contentTextView.setTextColor(context.getColor(R.color.dark))

                overviewPinnedViewHolder.imageContentView.imageTintList = ColorStateList.valueOf(context.getColor(R.color.dark))
                overviewPinnedViewHolder.audioContentView.imageTintList = ColorStateList.valueOf(context.getColor(R.color.dark))

            }
            ThemeType.ThemeDark -> {

                val tintedBackgroundDrawable =
                    context.getDrawable(R.drawable.round_corner_background)
                tintedBackgroundDrawable?.setTint(context.getColor(R.color.light_transparent))

                overviewPinnedViewHolder.rootItemContentView.background = tintedBackgroundDrawable

                val imageContentBackground = context.getDrawable(R.drawable.round_corner_background)
                imageContentBackground?.setTint(context.getColor(R.color.light_transparent))

                overviewPinnedViewHolder.contentImageView.background = imageContentBackground

                overviewPinnedViewHolder.titleTextView.setTextColor(context.getColor(R.color.light))
                overviewPinnedViewHolder.contentTextView.setTextColor(context.getColor(R.color.light))

                overviewPinnedViewHolder.imageContentView.imageTintList = null
                overviewPinnedViewHolder.audioContentView.imageTintList = null

            }
        }

    }

    override fun onBindViewHolder(overviewPinnedViewHolder: OverviewPinnedViewHolder, position: Int) {

        Firebase.auth.currentUser?.let {

            overviewPinnedViewHolder.titleTextView.text = context.contentEncryption.decryptEncodedData(
                notesDataStructureList[position].noteTile.toString(),
                it.uid
            )

            overviewPinnedViewHolder.contentTextView.text = context.contentEncryption.decryptEncodedData(
                notesDataStructureList[position].noteTextContent.toString(),
                it.uid
            )

        }

        when (context.themePreferences.checkThemeLightDark()) {
            ThemeType.ThemeLight -> {

                val tintedBackgroundDrawable =
                    context.getDrawable(R.drawable.round_corner_background)
                tintedBackgroundDrawable?.setTint(context.getColor(R.color.dark_transparent))

                overviewPinnedViewHolder.rootItemContentView.background = tintedBackgroundDrawable

                val imageContentBackground = context.getDrawable(R.drawable.round_corner_background)
                imageContentBackground?.setTint(context.getColor(R.color.dark_transparent))

                overviewPinnedViewHolder.contentImageView.background = imageContentBackground

                overviewPinnedViewHolder.titleTextView.setTextColor(context.getColor(R.color.dark))
                overviewPinnedViewHolder.contentTextView.setTextColor(context.getColor(R.color.dark))

                overviewPinnedViewHolder.imageContentView.imageTintList = ColorStateList.valueOf(context.getColor(R.color.dark))
                overviewPinnedViewHolder.audioContentView.imageTintList = ColorStateList.valueOf(context.getColor(R.color.dark))

            }
            ThemeType.ThemeDark -> {

                val tintedBackgroundDrawable =
                    context.getDrawable(R.drawable.round_corner_background)
                tintedBackgroundDrawable?.setTint(context.getColor(R.color.light_transparent))

                overviewPinnedViewHolder.rootItemContentView.background = tintedBackgroundDrawable

                val imageContentBackground = context.getDrawable(R.drawable.round_corner_background)
                imageContentBackground?.setTint(context.getColor(R.color.light_transparent))

                overviewPinnedViewHolder.contentImageView.background = imageContentBackground

                overviewPinnedViewHolder.titleTextView.setTextColor(context.getColor(R.color.light))
                overviewPinnedViewHolder.contentTextView.setTextColor(context.getColor(R.color.light))

                overviewPinnedViewHolder.imageContentView.imageTintList = null
                overviewPinnedViewHolder.audioContentView.imageTintList = null

            }
        }

        if (notesDataStructureList[position].noteHandwritingSnapshotLink == null) {

            overviewPinnedViewHolder.contentImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            overviewPinnedViewHolder.contentImageView.setColorFilter(context.getColor(R.color.pink_transparent))
            overviewPinnedViewHolder.contentImageView.setImageDrawable(context.getDrawable(R.drawable.vector_icon_no_content))

            overviewPinnedViewHolder.contentImageView.visibility = View.GONE

        } else {

            overviewPinnedViewHolder.contentImageView.scaleType = ImageView.ScaleType.FIT_CENTER
            overviewPinnedViewHolder.contentImageView.clearColorFilter()

            overviewPinnedViewHolder.contentImageView.visibility = View.VISIBLE

            Glide.with(context)
                .load(notesDataStructureList[position].noteHandwritingSnapshotLink)
                .into(overviewPinnedViewHolder.contentImageView)

        }

        if (notesDataStructureList[position].noteTile.isNullOrBlank()) {

            overviewPinnedViewHolder.titleTextView.visibility = View.GONE

        } else {

            notesDataStructureList[position].noteTile?.let { noteTile ->

                if (noteTile.isNotBlank()) {

                    overviewPinnedViewHolder.titleTextView.visibility = View.VISIBLE

                } else {

                    overviewPinnedViewHolder.titleTextView.visibility = View.GONE

                }

            }

        }

        if (notesDataStructureList[position].noteTextContent.isNullOrBlank()) {

            overviewPinnedViewHolder.contentTextView.visibility = View.GONE

        } else {

            notesDataStructureList[position].noteTextContent?.let { textContent ->

                if (textContent.isNotBlank()) {

                    overviewPinnedViewHolder.contentTextView.visibility = View.VISIBLE

                } else {

                    overviewPinnedViewHolder.contentTextView.visibility = View.GONE

                }

            }

        }

        if (notesDataStructureList[position].noteImagePaths == null) {

            overviewPinnedViewHolder.imageContentView.visibility = View.GONE

        } else {

            notesDataStructureList[position].noteImagePaths?.let {

                if (it.isBlank()) {

                    overviewPinnedViewHolder.imageContentView.visibility = View.GONE

                } else {

                    overviewPinnedViewHolder.imageContentView.visibility = View.VISIBLE

                }

            }

        }

        if (notesDataStructureList[position].noteVoicePaths == null) {

                overviewPinnedViewHolder.audioContentView.visibility = View.GONE

        } else {

            notesDataStructureList[position].noteVoicePaths?.let {

                if (it.isBlank()) {

                    overviewPinnedViewHolder.audioContentView.visibility = View.GONE

                } else {

                    overviewPinnedViewHolder.audioContentView.visibility = View.VISIBLE

                }

            }

        }

        overviewPinnedViewHolder.titleTextView.setOnClickListener {

            overviewPinnedViewHolder.waitingViewLoading.visibility = View.VISIBLE

            context.noteDatabaseConfigurations.updatedDatabaseItemPosition(position)
            context.noteDatabaseConfigurations.updatedDatabaseItemIdentifier(notesDataStructureList[position].uniqueNoteId)

            val paintingPathsJsonArray = notesDataStructureList[position].noteHandwritingPaintingPaths

            if (paintingPathsJsonArray.isNullOrEmpty()) {

                overviewPinnedViewHolder.waitingViewLoading.visibility = View.GONE

                TakeNote.open(context = context,
                    incomingActivityName = this@OverviewAdapterPinned.javaClass.simpleName,
                    extraConfigurations = TakeNote.NoteConfigurations.KeyboardTyping,
                    uniqueNoteId = notesDataStructureList[position].uniqueNoteId,
                    noteTile = notesDataStructureList[position].noteTile,
                    contentText = notesDataStructureList[position].noteTextContent,
                    encryptedTextContent = true,
                    updateExistingNote = true,
                    pinnedNote = true
                )

            } else {

                overviewPinnedViewHolder.waitingViewLoading.visibility = View.GONE

                TakeNote.open(context = context,
                    incomingActivityName = this@OverviewAdapterPinned.javaClass.simpleName,
                    extraConfigurations = TakeNote.NoteConfigurations.KeyboardTyping,
                    uniqueNoteId = notesDataStructureList[position].uniqueNoteId,
                    noteTile = notesDataStructureList[position].noteTile,
                    contentText = notesDataStructureList[position].noteTextContent,
                    paintingPath = paintingPathsJsonArray,
                    encryptedTextContent = true,
                    updateExistingNote = true,
                    pinnedNote = true
                )

            }

        }

        overviewPinnedViewHolder.rootItemContentView.setOnClickListener {

            overviewPinnedViewHolder.waitingViewLoading.visibility = View.VISIBLE

            context.noteDatabaseConfigurations.updatedDatabaseItemPosition(position)
            context.noteDatabaseConfigurations.updatedDatabaseItemIdentifier(notesDataStructureList[position].uniqueNoteId)

            val paintingPathsJsonArray = notesDataStructureList[position].noteHandwritingPaintingPaths

            if (paintingPathsJsonArray.isNullOrEmpty()) {

                overviewPinnedViewHolder.waitingViewLoading.visibility = View.GONE

                TakeNote.open(context = context,
                    incomingActivityName = this@OverviewAdapterPinned.javaClass.simpleName,
                    extraConfigurations = TakeNote.NoteConfigurations.KeyboardTyping,
                    uniqueNoteId = notesDataStructureList[position].uniqueNoteId,
                    noteTile = notesDataStructureList[position].noteTile,
                    contentText = notesDataStructureList[position].noteTextContent,
                    encryptedTextContent = true,
                    updateExistingNote = true
                )

            } else {

                overviewPinnedViewHolder.waitingViewLoading.visibility = View.GONE

                TakeNote.open(context = context,
                    incomingActivityName = this@OverviewAdapterPinned.javaClass.simpleName,
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