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
import net.geeksempire.ready.keep.notes.Overview.UserInterface.Adapter.ViewHolders.OverviewUnpinnedViewHolder
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.PopupOptionsMenu.BalloonOptionsMenu

class OverviewAdapterUnpinned(val context: KeepNoteOverview) : RecyclerView.Adapter<OverviewUnpinnedViewHolder>() {

    val notesDataStructureList: ArrayList<NotesDatabaseModel> = ArrayList<NotesDatabaseModel>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : OverviewUnpinnedViewHolder {

        return OverviewUnpinnedViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.overview_unpinned_notes_item, viewGroup, false))
    }

    override fun getItemCount() : Int {

        return notesDataStructureList.size
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)

        return notesDataStructureList[position].dataSelected
    }

    override fun onBindViewHolder(overviewUnpinnedViewHolder: OverviewUnpinnedViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(overviewUnpinnedViewHolder, position, payloads)

        when (context.themePreferences.checkThemeLightDark()) {
            ThemeType.ThemeLight -> {

                val tintedBackgroundDrawable =
                    context.getDrawable(R.drawable.round_corner_background)
                tintedBackgroundDrawable?.setTint(context.getColor(R.color.dark_transparent))

                overviewUnpinnedViewHolder.rootItemContentView.background = tintedBackgroundDrawable

                val imageContentBackground = context.getDrawable(R.drawable.round_corner_background)
                imageContentBackground?.setTint(context.getColor(R.color.dark_transparent))

                overviewUnpinnedViewHolder.contentImageView.background = imageContentBackground

                overviewUnpinnedViewHolder.titleTextView.setTextColor(context.getColor(R.color.dark))
                overviewUnpinnedViewHolder.contentTextView.setTextColor(context.getColor(R.color.dark))

                overviewUnpinnedViewHolder.imageContentView.imageTintList = ColorStateList.valueOf(context.getColor(R.color.dark))
                overviewUnpinnedViewHolder.audioContentView.imageTintList = ColorStateList.valueOf(context.getColor(R.color.dark))

            }
            ThemeType.ThemeDark -> {

                val tintedBackgroundDrawable =
                    context.getDrawable(R.drawable.round_corner_background)
                tintedBackgroundDrawable?.setTint(context.getColor(R.color.light_transparent))

                overviewUnpinnedViewHolder.rootItemContentView.background = tintedBackgroundDrawable

                val imageContentBackground = context.getDrawable(R.drawable.round_corner_background)
                imageContentBackground?.setTint(context.getColor(R.color.light_transparent))

                overviewUnpinnedViewHolder.contentImageView.background = imageContentBackground

                overviewUnpinnedViewHolder.titleTextView.setTextColor(context.getColor(R.color.light))
                overviewUnpinnedViewHolder.contentTextView.setTextColor(context.getColor(R.color.light))

                overviewUnpinnedViewHolder.imageContentView.imageTintList = null
                overviewUnpinnedViewHolder.audioContentView.imageTintList = null

            }
        }

    }

    override fun onBindViewHolder(overviewUnpinnedViewHolder: OverviewUnpinnedViewHolder, position: Int) {

        Firebase.auth.currentUser?.let {

            overviewUnpinnedViewHolder.titleTextView.text = context.contentEncryption.decryptEncodedData(
                notesDataStructureList[position].noteTile.toString(),
                it.uid
            )

            overviewUnpinnedViewHolder.contentTextView.text = context.contentEncryption.decryptEncodedData(
                notesDataStructureList[position].noteTextContent.toString(),
                it.uid
            )

        }

        when (context.themePreferences.checkThemeLightDark()) {
            ThemeType.ThemeLight -> {

                val tintedBackgroundDrawable =
                    context.getDrawable(R.drawable.round_corner_background)
                tintedBackgroundDrawable?.setTint(context.getColor(R.color.dark_transparent))

                overviewUnpinnedViewHolder.rootItemContentView.background = tintedBackgroundDrawable

                val imageContentBackground = context.getDrawable(R.drawable.round_corner_background)
                imageContentBackground?.setTint(context.getColor(R.color.dark_transparent))

                overviewUnpinnedViewHolder.contentImageView.background = imageContentBackground

                overviewUnpinnedViewHolder.titleTextView.setTextColor(context.getColor(R.color.dark))
                overviewUnpinnedViewHolder.contentTextView.setTextColor(context.getColor(R.color.dark))

                overviewUnpinnedViewHolder.imageContentView.imageTintList = ColorStateList.valueOf(context.getColor(R.color.dark))
                overviewUnpinnedViewHolder.audioContentView.imageTintList = ColorStateList.valueOf(context.getColor(R.color.dark))

            }
            ThemeType.ThemeDark -> {

                val tintedBackgroundDrawable =
                    context.getDrawable(R.drawable.round_corner_background)
                tintedBackgroundDrawable?.setTint(context.getColor(R.color.light_transparent))

                overviewUnpinnedViewHolder.rootItemContentView.background = tintedBackgroundDrawable

                val imageContentBackground = context.getDrawable(R.drawable.round_corner_background)
                imageContentBackground?.setTint(context.getColor(R.color.light_transparent))

                overviewUnpinnedViewHolder.contentImageView.background = imageContentBackground

                overviewUnpinnedViewHolder.titleTextView.setTextColor(context.getColor(R.color.light))
                overviewUnpinnedViewHolder.contentTextView.setTextColor(context.getColor(R.color.light))

                overviewUnpinnedViewHolder.imageContentView.imageTintList = null
                overviewUnpinnedViewHolder.audioContentView.imageTintList = null

            }
        }

        if (notesDataStructureList[position].noteHandwritingSnapshotLink == null) {

            overviewUnpinnedViewHolder.contentImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            overviewUnpinnedViewHolder.contentImageView.setColorFilter(context.getColor(R.color.pink_transparent))
            overviewUnpinnedViewHolder.contentImageView.setImageDrawable(context.getDrawable(R.drawable.vector_icon_no_content))

            overviewUnpinnedViewHolder.contentImageView.visibility = View.GONE

        } else {

            overviewUnpinnedViewHolder.contentImageView.scaleType = ImageView.ScaleType.FIT_CENTER
            overviewUnpinnedViewHolder.contentImageView.clearColorFilter()

            overviewUnpinnedViewHolder.contentImageView.visibility = View.VISIBLE

            Glide.with(context)
                .load(notesDataStructureList[position].noteHandwritingSnapshotLink)
                .into(overviewUnpinnedViewHolder.contentImageView)

        }

        if (notesDataStructureList[position].noteTile.isNullOrBlank()) {

            overviewUnpinnedViewHolder.titleTextView.visibility = View.GONE

        } else {

            notesDataStructureList[position].noteTile?.let { noteTile ->

                if (noteTile.isNotBlank()) {

                    overviewUnpinnedViewHolder.titleTextView.visibility = View.VISIBLE

                } else {

                    overviewUnpinnedViewHolder.titleTextView.visibility = View.GONE

                }

            }

        }

        if (notesDataStructureList[position].noteTextContent.isNullOrBlank()) {

            overviewUnpinnedViewHolder.contentTextView.visibility = View.GONE

        } else {

            notesDataStructureList[position].noteTextContent?.let { textContent ->

                if (textContent.isNotBlank()) {

                    overviewUnpinnedViewHolder.contentTextView.visibility = View.VISIBLE

                } else {

                    overviewUnpinnedViewHolder.contentTextView.visibility = View.GONE

                }

            }

        }

        if (notesDataStructureList[position].noteImagePaths == null) {

            overviewUnpinnedViewHolder.imageContentView.visibility = View.GONE

        } else {

            notesDataStructureList[position].noteImagePaths?.let {

                if (it.isBlank()) {

                    overviewUnpinnedViewHolder.imageContentView.visibility = View.GONE

                } else {

                    overviewUnpinnedViewHolder.imageContentView.visibility = View.VISIBLE

                }

            }

        }

        if (notesDataStructureList[position].noteVoicePaths == null) {

                overviewUnpinnedViewHolder.audioContentView.visibility = View.GONE

        } else {

            notesDataStructureList[position].noteVoicePaths?.let {

                if (it.isBlank()) {

                    overviewUnpinnedViewHolder.audioContentView.visibility = View.GONE

                } else {

                    overviewUnpinnedViewHolder.audioContentView.visibility = View.VISIBLE

                }

            }

        }

        overviewUnpinnedViewHolder.titleTextView.setOnClickListener { view ->

//            overviewUnpinnedViewHolder.waitingViewLoading.visibility = View.VISIBLE
//
//            context.noteDatabaseConfigurations.updatedDatabaseItemPosition(position)
//            context.noteDatabaseConfigurations.updatedDatabaseItemIdentifier(notesDataStructureList[position].uniqueNoteId)
//
//            val paintingPathsJsonArray = notesDataStructureList[position].noteHandwritingPaintingPaths
//
//            if (paintingPathsJsonArray.isNullOrEmpty()) {
//
//                overviewUnpinnedViewHolder.waitingViewLoading.visibility = View.GONE
//
//                TakeNote.open(context = context,
//                    incomingActivityName = this@OverviewAdapterUnpinned.javaClass.simpleName,
//                    extraConfigurations = TakeNote.NoteConfigurations.KeyboardTyping,
//                    uniqueNoteId = notesDataStructureList[position].uniqueNoteId,
//                    noteTile = notesDataStructureList[position].noteTile,
//                    contentText = notesDataStructureList[position].noteTextContent,
//                    encryptedTextContent = true,
//                    updateExistingNote = true
//                )
//
//            } else {
//
//                overviewUnpinnedViewHolder.waitingViewLoading.visibility = View.GONE
//
//                TakeNote.open(context = context,
//                    incomingActivityName = this@OverviewAdapterUnpinned.javaClass.simpleName,
//                    extraConfigurations = TakeNote.NoteConfigurations.KeyboardTyping,
//                    uniqueNoteId = notesDataStructureList[position].uniqueNoteId,
//                    noteTile = notesDataStructureList[position].noteTile,
//                    contentText = notesDataStructureList[position].noteTextContent,
//                    paintingPath = paintingPathsJsonArray,
//                    encryptedTextContent = true,
//                    updateExistingNote = true
//                )
//
//            }

        }

        overviewUnpinnedViewHolder.rootItemContentView.setOnClickListener { view ->

            val balloonOptionsMenu = BalloonOptionsMenu(context, context.overviewLayoutBinding.rootView, view)

            balloonOptionsMenu.initializeBalloonPosition()

//            overviewUnpinnedViewHolder.waitingViewLoading.visibility = View.VISIBLE
//
//            context.noteDatabaseConfigurations.updatedDatabaseItemPosition(position)
//            context.noteDatabaseConfigurations.updatedDatabaseItemIdentifier(notesDataStructureList[position].uniqueNoteId)
//
//            val paintingPathsJsonArray = notesDataStructureList[position].noteHandwritingPaintingPaths
//
//            if (paintingPathsJsonArray.isNullOrEmpty()) {
//
//                overviewUnpinnedViewHolder.waitingViewLoading.visibility = View.GONE
//
//                TakeNote.open(context = context,
//                    incomingActivityName = this@OverviewAdapterUnpinned.javaClass.simpleName,
//                    extraConfigurations = TakeNote.NoteConfigurations.KeyboardTyping,
//                    uniqueNoteId = notesDataStructureList[position].uniqueNoteId,
//                    noteTile = notesDataStructureList[position].noteTile,
//                    contentText = notesDataStructureList[position].noteTextContent,
//                    encryptedTextContent = true,
//                    updateExistingNote = true
//                )
//
//            } else {
//
//                overviewUnpinnedViewHolder.waitingViewLoading.visibility = View.GONE
//
//                TakeNote.open(context = context,
//                    incomingActivityName = this@OverviewAdapterUnpinned.javaClass.simpleName,
//                    extraConfigurations = TakeNote.NoteConfigurations.KeyboardTyping,
//                    uniqueNoteId = notesDataStructureList[position].uniqueNoteId,
//                    noteTile = notesDataStructureList[position].noteTile,
//                    contentText = notesDataStructureList[position].noteTextContent,
//                    paintingPath = paintingPathsJsonArray,
//                    encryptedTextContent = true,
//                    updateExistingNote = true
//                )
//
//            }

        }

    }

}