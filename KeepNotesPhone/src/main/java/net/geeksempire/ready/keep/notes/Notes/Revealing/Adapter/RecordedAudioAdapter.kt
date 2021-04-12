/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Notes.Revealing.Adapter

import android.content.res.ColorStateList
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.Notes.Revealing.Adapter.ViewHolder.RecordedAudioViewHolder
import net.geeksempire.ready.keep.notes.Notes.Revealing.DataStructure.RecordedAudioDataStructure
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemePreferences
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R

class RecordedAudioAdapter(private val context: AppCompatActivity, val recyclerView: RecyclerView) : RecyclerView.Adapter<RecordedAudioViewHolder>() {

    val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer()
    }

    val themePreferences: ThemePreferences by lazy {
        ThemePreferences(context)
    }

    private val databaseEndpoints: DatabaseEndpoints by lazy {
        DatabaseEndpoints()
    }

    val recordedAudioData: ArrayList<RecordedAudioDataStructure> = ArrayList<RecordedAudioDataStructure>()

    var currentRecordedAudioPlaying: Int = 0

    object MediaPlayerAction {
        const val STOP = 0
        const val PAUSE = 1
        const val PLAY = 2
    }

    var setMediaPlayerAction: Int = MediaPlayerAction.STOP

    init {

        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )


    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : RecordedAudioViewHolder {

        return RecordedAudioViewHolder(LayoutInflater.from(context).inflate(R.layout.recorded_audio_items, viewGroup, false))
    }

    override fun getItemCount() : Int {

        return recordedAudioData.size
    }

    override fun onBindViewHolder(recordedAudioViewHolder: RecordedAudioViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(recordedAudioViewHolder, position, payloads)

        if (recordedAudioData[position].audioPlaying) {

            recordedAudioViewHolder.audioProgressBar.progress = 0f
            recordedAudioViewHolder.audioProgressBar.stopProgressAnimation()

            recordedAudioViewHolder.audioPlayPause.icon = context.getDrawable(android.R.drawable.ic_media_pause)

        } else {

            recordedAudioViewHolder.audioProgressBar.progress = 0f
            recordedAudioViewHolder.audioProgressBar.stopProgressAnimation()

            recordedAudioViewHolder.audioPlayPause.icon = context.getDrawable(android.R.drawable.ic_media_play)

        }

    }

    override fun onBindViewHolder(recordedAudioViewHolder: RecordedAudioViewHolder, position: Int) {

        when (themePreferences.checkThemeLightDark()) {
            ThemeType.ThemeLight -> {

                recordedAudioViewHolder.audioPlayPause.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.default_color_game))
                recordedAudioViewHolder.audioDelete.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.default_color_game))

                recordedAudioViewHolder.audioProgressBar.setTrackColor(context.getColor(R.color.white))

            }
            ThemeType.ThemeDark -> {

                recordedAudioViewHolder.audioPlayPause.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.default_color_game_transparent))
                recordedAudioViewHolder.audioDelete.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.default_color_game_transparent))

                recordedAudioViewHolder.audioProgressBar.setTrackColor(context.getColor(R.color.black))

            }
        }

        if (recordedAudioData[position].audioPlaying) {

            recordedAudioViewHolder.audioProgressBar.progress = 0f
            recordedAudioViewHolder.audioProgressBar.stopProgressAnimation()

            recordedAudioViewHolder.audioPlayPause.icon = context.getDrawable(android.R.drawable.ic_media_pause)

        } else {

            recordedAudioViewHolder.audioProgressBar.progress = 0f
            recordedAudioViewHolder.audioProgressBar.stopProgressAnimation()

            recordedAudioViewHolder.audioPlayPause.icon = context.getDrawable(android.R.drawable.ic_media_play)

        }

        recordedAudioViewHolder.audioDelete.setOnClickListener {
            Log.d(this@RecordedAudioAdapter.javaClass.simpleName, "Recorded Audio To Remove: ${recordedAudioData[position]}")

            CoroutineScope(Dispatchers.IO).launch {

                Firebase.auth.currentUser?.let { firebaseUser ->

                    Firebase.storage.reference
                        .child(databaseEndpoints.voiceRecordingEndpoint(firebaseUser.uid, recordedAudioData[position].documentId).plus("/${recordedAudioData[position].audioRecordedId}" + ".MP3"))
                        .delete().addOnSuccessListener {

                        }.addOnFailureListener {

                        }

                }

                if (recordedAudioData[position].audioFile.delete()) {

                    withContext(Dispatchers.Main) {

                        recordedAudioData.apply {

                            if (recordedAudioData.size == 1) {

                                clear()

                            } else {

                                removeAt(position)

                            }

                        }

                        notifyItemRemoved(position)
                        notifyDataSetChanged()

                    }

                }

            }

        }

        recordedAudioViewHolder.audioPlayPause.setOnClickListener {

            if (mediaPlayer.isPlaying) {

                if (position == currentRecordedAudioPlaying) {

                    recordedAudioViewHolder.audioPlayPause.icon = context.getDrawable(android.R.drawable.ic_media_play)

                    mediaPlayer.pause()
                    recordedAudioViewHolder.audioProgressBar.stopProgressAnimation()

                    setMediaPlayerAction = MediaPlayerAction.PAUSE

                    recordedAudioData[position].audioPlaying = false
                    notifyDataSetChanged()

                } else {

                    recordedAudioData[currentRecordedAudioPlaying].audioPlaying = false
                    recordedAudioData[position].audioPlaying = true
                    notifyDataSetChanged()

                    recordedAudioViewHolder.audioProgressBar.progress = 0f

                    mediaPlayer.stop()
                    mediaPlayer.reset()

                    setMediaPlayerAction = MediaPlayerAction.STOP

                    recordedAudioViewHolder.audioPlayPause.icon = context.getDrawable(android.R.drawable.ic_media_pause)

                    currentRecordedAudioPlaying = position

                    mediaPlayer.setDataSource(recordedAudioData[position].audioFilePath)
                    mediaPlayer.prepare()

                    mediaPlayer.setOnPreparedListener {

                        mediaPlayer.start()

                        setMediaPlayerAction = MediaPlayerAction.PLAY

                        recordedAudioViewHolder.audioProgressBar.setStartProgress(0f)
                        recordedAudioViewHolder.audioProgressBar.setEndProgress(100f)

                        recordedAudioViewHolder.audioProgressBar.setProgressDuration(mediaPlayer.duration)
                        recordedAudioViewHolder.audioProgressBar.startProgressAnimation()

                    }

                    mediaPlayer.setOnSeekCompleteListener {

                    }

                    mediaPlayer.setOnCompletionListener {

                        mediaPlayer.stop()
                        mediaPlayer.reset()

                    }

                }

            } else {

                recordedAudioData[currentRecordedAudioPlaying].audioPlaying = false
                recordedAudioData[position].audioPlaying = true
                notifyDataSetChanged()

                recordedAudioViewHolder.audioProgressBar.progress = 0f

                mediaPlayer.stop()
                mediaPlayer.reset()

                setMediaPlayerAction = MediaPlayerAction.STOP

                recordedAudioViewHolder.audioPlayPause.icon = context.getDrawable(android.R.drawable.ic_media_pause)

                currentRecordedAudioPlaying = position

                mediaPlayer.setDataSource(recordedAudioData[position].audioFilePath)
                mediaPlayer.prepare()

                mediaPlayer.setOnPreparedListener {

                    mediaPlayer.start()

                    setMediaPlayerAction = MediaPlayerAction.PLAY

                    recordedAudioViewHolder.audioProgressBar.setStartProgress(0f)
                    recordedAudioViewHolder.audioProgressBar.setEndProgress(100f)

                    recordedAudioViewHolder.audioProgressBar.setProgressDuration(mediaPlayer.duration)
                    recordedAudioViewHolder.audioProgressBar.startProgressAnimation()

                }

                mediaPlayer.setOnSeekCompleteListener {

                }

                mediaPlayer.setOnCompletionListener {

                    mediaPlayer.stop()
                    mediaPlayer.reset()

                }

            }

        }

    }

}