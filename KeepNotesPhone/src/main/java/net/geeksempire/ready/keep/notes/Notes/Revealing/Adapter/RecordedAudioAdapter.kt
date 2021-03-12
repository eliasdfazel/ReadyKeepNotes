package net.geeksempire.ready.keep.notes.Notes.Revealing.Adapter

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import net.geeksempire.ready.keep.notes.Notes.Revealing.Adapter.ViewHolder.RecordedAudioViewHolder
import net.geeksempire.ready.keep.notes.Notes.Revealing.DataStructure.RecordedAudioDataStructure
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemePreferences
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R

class RecordedAudioAdapter(private val context: AppCompatActivity) : RecyclerView.Adapter<RecordedAudioViewHolder>() {

    val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer()
    }

    val themePreferences: ThemePreferences by lazy {
        ThemePreferences(context)
    }

    val recordedAudioData: ArrayList<RecordedAudioDataStructure> = ArrayList<RecordedAudioDataStructure>()

    var currentRecordedAudioPlaying: String? = null

    init {

        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )


    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : RecordedAudioViewHolder {

        return RecordedAudioViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.recorded_audio_items, viewGroup, false)
        )
    }

    override fun getItemCount() : Int {

        return recordedAudioData.size
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

    override fun onBindViewHolder(recordedAudioViewHolder: RecordedAudioViewHolder, position: Int) {

        recordedAudioViewHolder.audioPlayPause.setOnClickListener {

            if (recordedAudioData[position].audioFileName == currentRecordedAudioPlaying) {



            }

            if (mediaPlayer.isPlaying) {

                recordedAudioViewHolder.audioPlayPause.icon = context.getDrawable(android.R.drawable.ic_media_play)

                mediaPlayer.pause()
                recordedAudioViewHolder.audioProgressBar.stopProgressAnimation()

            } else {

                recordedAudioViewHolder.audioProgressBar.progress = 0f

                mediaPlayer.stop()
                mediaPlayer.reset()

                recordedAudioViewHolder.audioPlayPause.icon = context.getDrawable(android.R.drawable.ic_media_pause)

                currentRecordedAudioPlaying = recordedAudioData[position].audioFileName

                mediaPlayer.setDataSource(recordedAudioData[position].audioFilePath)
                mediaPlayer.prepare()

                mediaPlayer.setOnPreparedListener {

                    mediaPlayer.start()

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