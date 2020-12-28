package net.geeksempire.ready.keep.notes.Overview.UserInterface.Adapter

import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.geeksempire.ready.keep.notes.Database.DataStructure.Notes
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R

class OverviewAdapter (private val context: KeepNoteOverview) : RecyclerView.Adapter<OverviewViewHolder>() {

    val notesDataStructureList: ArrayList<DocumentSnapshot> = ArrayList<DocumentSnapshot>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : OverviewViewHolder {

        return OverviewViewHolder(LayoutInflater.from(context).inflate(R.layout.overview_saved_notes_item, viewGroup, false))
    }

    override fun getItemCount() : Int {

        return notesDataStructureList.size
    }

    override fun onBindViewHolder(overviewViewHolder: OverviewViewHolder, position: Int) {

        context.firebaseUser?.let {

            overviewViewHolder.titleTextView.text = context.contentEncryption.decryptEncodedData(notesDataStructureList[position][Notes.NoteTile].toString(), context.firebaseUser.uid)

            overviewViewHolder.contentTextView.text = context.contentEncryption.decryptEncodedData(notesDataStructureList[position][Notes.NoteTextContent].toString(), context.firebaseUser.uid)

        }

        when (context.themePreferences.checkThemeLightDark()) {
            ThemeType.ThemeLight -> {

                val tintedBackgroundDrawable = context.getDrawable(R.drawable.round_corner_background)
                tintedBackgroundDrawable?.setTint(context.getColor(R.color.dark_transparent))

                overviewViewHolder.rootItemView.background = tintedBackgroundDrawable

                val imageContentBackground = context.getDrawable(R.drawable.round_corner_background)
                imageContentBackground?.setTint(context.getColor(R.color.dark_transparent))

                overviewViewHolder.contentImageView.background = imageContentBackground

                overviewViewHolder.titleTextView.setTextColor(context.getColor(R.color.dark))
                overviewViewHolder.contentTextView.setTextColor(context.getColor(R.color.dark))

            }
            ThemeType.ThemeDark -> {

                val tintedBackgroundDrawable = context.getDrawable(R.drawable.round_corner_background)
                tintedBackgroundDrawable?.setTint(context.getColor(R.color.light_transparent))

                overviewViewHolder.rootItemView.background = tintedBackgroundDrawable

                val imageContentBackground = context.getDrawable(R.drawable.round_corner_background)
                imageContentBackground?.setTint(context.getColor(R.color.light_transparent))

                overviewViewHolder.contentImageView.background = imageContentBackground

                overviewViewHolder.titleTextView.setTextColor(context.getColor(R.color.light))
                overviewViewHolder.contentTextView.setTextColor(context.getColor(R.color.light))

            }
        }

        if (notesDataStructureList[position][Notes.NoteHandwritingSnapshotLink] == null) {

            overviewViewHolder.contentImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            overviewViewHolder.contentImageView.setColorFilter(context.getColor(R.color.pink_transparent))
            overviewViewHolder.contentImageView.setImageDrawable(context.getDrawable(R.drawable.icon_no_content))

        } else {

            overviewViewHolder.contentImageView.scaleType = ImageView.ScaleType.FIT_CENTER
            overviewViewHolder.contentImageView.clearColorFilter()

            Glide.with(context)
                .load(notesDataStructureList[position][Notes.NoteHandwritingSnapshotLink].toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(overviewViewHolder.contentImageView)

        }

        overviewViewHolder.rootItemView.setOnClickListener {

            overviewViewHolder.waitingViewLoading.visibility = View.VISIBLE

            (context.application as KeepNoteApplication)
                .firestoreDatabase.collection(context.databaseEndpoints.paintPathsCollectionEndpoints(notesDataStructureList[position].reference.path))
                .get()
                .addOnSuccessListener { querySnapshot ->

                    if (querySnapshot.isEmpty) {

                        overviewViewHolder.waitingViewLoading.visibility = View.GONE

                        context.startActivity(Intent(context, TakeNote::class.java).apply {
                            putExtra(TakeNote.NoteTakingWritingType.DocumentId, notesDataStructureList[position].id.toLong())
                            putExtra(TakeNote.NoteTakingWritingType.TitleText, notesDataStructureList[position][Notes.NoteTile].toString())
                            putExtra(TakeNote.NoteTakingWritingType.ContentText, notesDataStructureList[position][Notes.NoteTextContent].toString())
                        }, ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, 0).toBundle())

                    } else {

                        TakeNote.paintingPathsJsonArray.clear()
                        TakeNote.paintingPathsJsonArray.addAll(querySnapshot.documents)

                        overviewViewHolder.waitingViewLoading.visibility = View.GONE

                        context.startActivity(Intent(context, TakeNote::class.java).apply {
                            putExtra(TakeNote.NoteTakingWritingType.DocumentId, notesDataStructureList[position].id.toLong())
                            putExtra(TakeNote.NoteTakingWritingType.TitleText, notesDataStructureList[position][Notes.NoteTile].toString())
                            putExtra(TakeNote.NoteTakingWritingType.ContentText, notesDataStructureList[position][Notes.NoteTextContent].toString())
                            putExtra(TakeNote.NoteTakingWritingType.PaintingPath, "Draw Saved Handwriting")
                        }, ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, 0).toBundle())

                    }

                }

        }

    }

    fun rearrangeItemsData(fromPosition: Int, toPosition: Int) = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

        val selectedItem = notesDataStructureList[fromPosition]
        notesDataStructureList.removeAt(fromPosition)

        if (toPosition < fromPosition) {

            notesDataStructureList.add(toPosition, selectedItem)

        } else {

            notesDataStructureList.add(toPosition - 1, selectedItem)

        }

    }

}