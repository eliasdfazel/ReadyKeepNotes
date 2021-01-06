package net.geeksempire.ready.keep.notes.Overview.UserInterface.Adapter

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesTemporaryModification
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.Data.percentage
import kotlin.math.abs


class OverviewAdapter(val context: KeepNoteOverview) : RecyclerView.Adapter<OverviewViewHolder>() {

    val notesDataStructureList: ArrayList<NotesDatabaseModel> = ArrayList<NotesDatabaseModel>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : OverviewViewHolder {

        return OverviewViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.overview_saved_notes_item,
                viewGroup,
                false
            )
        )
    }

    override fun getItemCount() : Int {

        return notesDataStructureList.size
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(overviewViewHolder: OverviewViewHolder, position: Int) {

        context.firebaseUser?.let {

            overviewViewHolder.titleTextView.text = context.contentEncryption.decryptEncodedData(
                notesDataStructureList[position].noteTile.toString(),
                context.firebaseUser.uid
            )

            overviewViewHolder.contentTextView.text = context.contentEncryption.decryptEncodedData(
                notesDataStructureList[position].noteTextContent.toString(),
                context.firebaseUser.uid
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

        when (notesDataStructureList[position].dataSelected) {
            NotesTemporaryModification.NoteIsSelected -> {
                Log.d(this@OverviewAdapter.javaClass.simpleName, "Note Is Selected")

                overviewViewHolder.optionsView.visibility = View.VISIBLE

                overviewViewHolder.rootItemContentView.x = -overviewViewHolder.optionsView.width.toFloat()

            }
            NotesTemporaryModification.NoteIsNotSelected -> {
                Log.d(this@OverviewAdapter.javaClass.simpleName, "Note Is Not Selected")

                overviewViewHolder.optionsView.visibility = View.INVISIBLE

                overviewViewHolder.rootItemContentView.x = 0f

            }
        }

        var differentiateX: Float = 0f

        overviewViewHolder.rootItemContentView.setOnTouchListener { view, motionEvent ->

            when (motionEvent.action) {

                MotionEvent.ACTION_DOWN -> {

                    differentiateX = overviewViewHolder.rootItemContentView.x - motionEvent.rawX

                }
                MotionEvent.ACTION_MOVE -> {

                    val dxAbsolute = abs(motionEvent.rawX + differentiateX)

                    if (abs(motionEvent.rawX) < abs(differentiateX)) {

                        if (dxAbsolute < overviewViewHolder.optionsView.width.percentage(66.6)) {

                            overviewViewHolder.rootItemContentView.x = motionEvent.rawX + differentiateX

                            if (!overviewViewHolder.optionsView.isShown) {

                                overviewViewHolder.optionsView.visibility = View.VISIBLE
                                overviewViewHolder.optionsView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))

                                notesDataStructureList[position].dataSelected = NotesTemporaryModification.NoteIsSelected

                            }

                        }

                    } else {



                    }

                }
                MotionEvent.ACTION_UP -> {

                    if (overviewViewHolder.optionsView.isShown) {

                        overviewViewHolder.rootItemContentView.x = -overviewViewHolder.optionsView.width.toFloat()

                    } else {



                    }

                }
                MotionEvent.ACTION_CANCEL -> {

                    if (overviewViewHolder.optionsView.isShown) {

                        overviewViewHolder.rootItemContentView.x = -overviewViewHolder.optionsView.width.toFloat()

                    } else {



                    }

                }
                else -> {


                }
            }

            false
        }

        overviewViewHolder.rootItemContentView.setOnClickListener {

            if (!overviewViewHolder.optionsView.isShown) {

                overviewViewHolder.waitingViewLoading.visibility = View.VISIBLE

                val paintingPathsJsonArray = notesDataStructureList[position].noteHandwritingPaintingPaths

                if (paintingPathsJsonArray.isNullOrEmpty()) {

                    overviewViewHolder.waitingViewLoading.visibility = View.GONE

                    context.startActivity(Intent(context, TakeNote::class.java).apply {
                        putExtra(TakeNote.NoteTakingWritingType.DocumentId, notesDataStructureList[position].uniqueNoteId)
                        putExtra(TakeNote.NoteTakingWritingType.TitleText, notesDataStructureList[position].noteTile)
                        putExtra(TakeNote.NoteTakingWritingType.ContentText, notesDataStructureList[position].noteTextContent)
                    }, ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, 0).toBundle())

                } else {

                    overviewViewHolder.waitingViewLoading.visibility = View.GONE

                    context.startActivity(Intent(context, TakeNote::class.java).apply {
                        putExtra(TakeNote.NoteTakingWritingType.DocumentId, notesDataStructureList[position].uniqueNoteId)
                        putExtra(TakeNote.NoteTakingWritingType.TitleText, notesDataStructureList[position].noteTile.toString())
                        putExtra(TakeNote.NoteTakingWritingType.ContentText, notesDataStructureList[position].noteTextContent.toString())
                        putExtra(TakeNote.NoteTakingWritingType.PaintingPath, paintingPathsJsonArray)
                    }, ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, 0).toBundle())

                }

            }

        }



        val valueAnimator = ValueAnimator.ofFloat(overviewViewHolder.optionsView.width.toFloat(), 0f)
        overviewViewHolder.closeOptionsMenu.setOnClickListener {

            valueAnimator.addUpdateListener {

                overviewViewHolder.rootItemContentView.x = -(it.animatedValue as Float)

            }
            valueAnimator.duration = 333
            valueAnimator.start()

            notesDataStructureList[position].dataSelected = NotesTemporaryModification.NoteIsNotSelected

            overviewViewHolder.optionsView.visibility = View.GONE
            overviewViewHolder.optionsView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))

        }

        context.overviewLayoutBinding.overviewRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {

                        overviewViewHolder.rootItemContentView.x = 0f

                        notesDataStructureList[position].dataSelected = NotesTemporaryModification.NoteIsNotSelected

                        if (overviewViewHolder.optionsView.isShown) {

                            overviewViewHolder.optionsView.visibility = View.GONE
                            overviewViewHolder.optionsView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))

                        }

                    }
                }

            }

        })

    }

}