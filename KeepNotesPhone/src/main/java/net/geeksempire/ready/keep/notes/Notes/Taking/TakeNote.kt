package net.geeksempire.ready.keep.notes.Notes.Taking

import android.animation.Animator
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.geeksempire.ready.keep.notes.Database.IO.AudioIO
import net.geeksempire.ready.keep.notes.Database.IO.NoteDatabaseConfigurations
import net.geeksempire.ready.keep.notes.Database.IO.NotesIO
import net.geeksempire.ready.keep.notes.Database.IO.PaintingIO
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.EntryConfigurations
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Notes.Taking.Extensions.setupAudioRecorderActions
import net.geeksempire.ready.keep.notes.Notes.Taking.Extensions.setupPaintingActions
import net.geeksempire.ready.keep.notes.Notes.Taking.Extensions.setupTakeNoteTheme
import net.geeksempire.ready.keep.notes.Notes.Taking.Extensions.setupToggleKeyboardHandwriting
import net.geeksempire.ready.keep.notes.Notes.Tools.AudioRecording.AudioRecordingLocalFile
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Adapter.RecentColorsAdapter
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.DataStructure.HandwritingSnapshotLocalFile
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Extensions.InputRecognizer
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Extensions.restorePaints
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.PaintingCanvasView
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Utils.StrokePaintingCanvasView
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemePreferences
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.Extensions.checkSpecialCharacters
import net.geeksempire.ready.keep.notes.Utils.Network.NetworkCheckpoint
import net.geeksempire.ready.keep.notes.Utils.Network.NetworkConnectionListener
import net.geeksempire.ready.keep.notes.Utils.Network.NetworkConnectionListenerInterface
import net.geeksempire.ready.keep.notes.Utils.Security.Encryption.ContentEncryption
import net.geeksempire.ready.keep.notes.Utils.UI.Display.displayX
import net.geeksempire.ready.keep.notes.Utils.UI.Display.displayY
import net.geeksempire.ready.keep.notes.databinding.TakeNoteLayoutBinding
import java.io.File
import javax.inject.Inject
import kotlin.math.hypot

class TakeNote : AppCompatActivity(), NetworkConnectionListenerInterface {

    val paintingCanvasView: PaintingCanvasView by lazy {
        PaintingCanvasView(this@TakeNote).also {
            it.setupPaintingPanel(
                getColor(R.color.default_color_bright),
                3.0f
            )
        }
    }

    val strokePaintingCanvasView: StrokePaintingCanvasView by lazy {
        StrokePaintingCanvasView(applicationContext).also {
            it.setupPaintingPanel(
                getColor(R.color.default_color_bright),
                3.0f
            )
        }
    }

    val themePreferences: ThemePreferences by lazy {
        ThemePreferences(applicationContext)
    }

    val notesIO: NotesIO by lazy {
        NotesIO(application as KeepNoteApplication)
    }

    val paintingIO: PaintingIO by lazy {
        PaintingIO(applicationContext)
    }

    val audioIO: AudioIO by lazy {
        AudioIO(application as KeepNoteApplication)
    }

    val noteDatabaseConfigurations: NoteDatabaseConfigurations by lazy {
        NoteDatabaseConfigurations(applicationContext)
    }

    val contentEncryption: ContentEncryption  = ContentEncryption()

    /**
     * True: Handwriting - False: Keyboard
     **/
    var toggleKeyboardHandwriting: Boolean = false

    val firebaseUser = Firebase.auth.currentUser

    val databaseEndpoints: DatabaseEndpoints = DatabaseEndpoints()

    val handwritingSnapshotLocalFile: HandwritingSnapshotLocalFile by lazy {
        HandwritingSnapshotLocalFile(this@TakeNote.externalMediaDirs[0].path)
    }

    val audioRecordingLocalFile: AudioRecordingLocalFile by lazy {
        AudioRecordingLocalFile(this@TakeNote.externalMediaDirs[0].path)
    }

    val recentColorsAdapter: RecentColorsAdapter by lazy {
        RecentColorsAdapter(this@TakeNote, paintingCanvasView)
    }

    var documentId = System.currentTimeMillis()

    var audioFileId: String? = null
    var audioFilePath: String? = null

    var autoEnterPlaced = false

    var contentDescriptionShowing = false

    var incomingActivity = EntryConfigurations::class.java.simpleName

    val inputRecognizer = InputRecognizer()

    @Inject lateinit var networkCheckpoint: NetworkCheckpoint

    @Inject lateinit var networkConnectionListener: NetworkConnectionListener

    lateinit var takeNoteLayoutBinding: TakeNoteLayoutBinding

    object NoteConfigurations {
        const val ExtraConfigurations = "NoteTakingWritingType"
        const val KeyboardTyping = "KeyboardTyping"
        const val Handwriting = "Handwriting"
        const val VoiceRecording = "VoiceRecording"

        const val EncryptedTextContent = "EncryptedTextContent"
        const val UpdateExistingNote = "UpdateExistingNote"

        const val AudioRecordRequestCode = 123

        const val PinnedNote = "PinnedNote"
    }

    object NoteExtraData {
        const val DocumentId = "DocumentId"
        const val TitleText = "TitleText"
        const val ContentText = "ContentText"
        const val PaintingPath = "PaintingPath"
    }

    companion object {

        fun open(context: Context,
                 incomingActivityName: String,
                 extraConfigurations: String,
                 uniqueNoteId: Long? = null,
                 noteTile: String? = null,
                 contentText: String? = null,
                 paintingPath: String? = null,
                 encryptedTextContent: Boolean,
                 updateExistingNote: Boolean = false,
                 pinnedNote: Boolean = false) {

            context.startActivity(Intent(context, TakeNote::class.java).apply {
                putExtra("IncomingActivityName", incomingActivityName)

                putExtra(TakeNote.NoteConfigurations.ExtraConfigurations, extraConfigurations)
                putExtra(TakeNote.NoteConfigurations.EncryptedTextContent, encryptedTextContent)
                putExtra(TakeNote.NoteConfigurations.UpdateExistingNote, updateExistingNote)

                putExtra(TakeNote.NoteExtraData.DocumentId, uniqueNoteId)
                putExtra(TakeNote.NoteExtraData.TitleText, noteTile)
                putExtra(TakeNote.NoteExtraData.ContentText, contentText)
                putExtra(TakeNote.NoteExtraData.PaintingPath, paintingPath)

                putExtra(TakeNote.NoteConfigurations.PinnedNote, pinnedNote)

                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }, ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, 0).toBundle())

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        takeNoteLayoutBinding = TakeNoteLayoutBinding.inflate(layoutInflater)
        setContentView(takeNoteLayoutBinding.root)

        (application as KeepNoteApplication)
            .dependencyGraph
            .subDependencyGraph()
            .create(this@TakeNote, takeNoteLayoutBinding.rootView)
            .inject(this@TakeNote)

        networkConnectionListener.networkConnectionListenerInterface = this@TakeNote

        setupTakeNoteTheme()

        setupToggleKeyboardHandwriting()

        setupPaintingActions()

        setupAudioRecorderActions()

        if (intent.hasExtra("IncomingActivityName")) {

            incomingActivity = intent.getStringExtra("IncomingActivityName")

        }

        firebaseUser?.let { firebaseUser ->

            documentId = if (intent.hasExtra(NoteExtraData.DocumentId)) {

                intent.getLongExtra(NoteExtraData.DocumentId, System.currentTimeMillis())

            } else {

                System.currentTimeMillis()

            }

            if (intent.hasExtra(NoteExtraData.TitleText)) {

                takeNoteLayoutBinding.editTextTitleView.setText(intent.getStringExtra(
                    NoteExtraData.TitleText
                )?.let {
                    contentEncryption.decryptEncodedData(it, firebaseUser.uid)
                })

            }

            if (intent.hasExtra(NoteExtraData.ContentText)
                && intent.hasExtra(NoteConfigurations.EncryptedTextContent)) {

                if (intent.getBooleanExtra(NoteConfigurations.EncryptedTextContent, false)) {

                    takeNoteLayoutBinding.editTextContentView.setText(intent.getStringExtra(NoteExtraData.ContentText)?.let { contentEncryption.decryptEncodedData(it, firebaseUser.uid) })

                } else {

                    takeNoteLayoutBinding.editTextContentView.setText(intent.getStringExtra(NoteExtraData.ContentText))

                }

                takeNoteLayoutBinding.editTextContentView.setSelection(takeNoteLayoutBinding.editTextContentView.text?.length?:0)

            }

            if (intent.hasExtra(NoteExtraData.PaintingPath)) {

                intent.getStringExtra(NoteExtraData.PaintingPath)?.let { paintingPathsData ->

                    paintingIO.preparePaintingPathsOffline(paintingCanvasView, paintingPathsData).invokeOnCompletion {

                        paintingCanvasView.restorePaints()

                    }

                }

            }

            takeNoteLayoutBinding.editTextContentView.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(editable: Editable?) {

                    editable?.let {

                        try {

                            if (editable[editable.length - 1] == '\n') {

                                val allLines = editable.toString().split("\n")

                                val lastLine = allLines[allLines.size - 2]

                                val specialCharacterData = lastLine.substring(IntRange(0, 0)).checkSpecialCharacters()

                                if (specialCharacterData.detected) {

                                    if (lastLine.length == 2) {

                                        autoEnterPlaced = true

                                        takeNoteLayoutBinding.editTextContentView.editableText.replace(editable.length - 4, editable.length, "")
                                        takeNoteLayoutBinding.editTextContentView.append("\n")

                                    } else {

                                        if (!autoEnterPlaced) {

                                            takeNoteLayoutBinding.editTextContentView.append(specialCharacterData.specialCharacter)
                                            takeNoteLayoutBinding.editTextContentView.setSelection(editable.length)

                                        }

                                        autoEnterPlaced = false

                                    }

                                }

                            }

                        } catch (e: IndexOutOfBoundsException) {
                            e.printStackTrace()
                        }

                    }

                }

            })

            takeNoteLayoutBinding.savingView.setOnClickListener {

                if (!takeNoteLayoutBinding.editTextContentView.text.isNullOrBlank() || paintingCanvasView.overallRedrawPaintingData.isNotEmpty()) {

                    if (!contentDescriptionShowing) {

                        notesIO.saveNotesAndPaintingOnline(
                            context = this@TakeNote,
                            firebaseUser = firebaseUser,
                            takeNoteLayoutBinding = takeNoteLayoutBinding,
                            databaseEndpoints = databaseEndpoints,
                            paintingIO = paintingIO,
                            paintingCanvasView = paintingCanvasView,
                            contentEncryption = contentEncryption,
                            documentId = documentId
                        )

                        notesIO.saveNotesAndPaintingOfflineRetry = notesIO.saveNotesAndPaintingOffline(
                            context = this@TakeNote,
                            firebaseUser = firebaseUser,
                            takeNoteLayoutBinding = takeNoteLayoutBinding,
                            paintingIO = paintingIO,
                            paintingCanvasView = paintingCanvasView,
                            contentEncryption = contentEncryption,
                            documentId = documentId
                        )

                    } else {



                    }

                }

            }

            takeNoteLayoutBinding.savedAudioRecordView.setOnClickListener {

                File(audioRecordingLocalFile.getAudioRecordingDirectoryPath(firebaseUser.uid, documentId.toString())).listFiles().forEach { recorded ->

                    println(">>> >> > " + recorded)

                }

            }

        }

    }

    override fun onResume() {
        super.onResume()



    }

    override fun onPause() {
        super.onPause()



    }

    override fun onBackPressed() {

        if (takeNoteLayoutBinding.colorPaletteInclude.root.isShown) {

            val finalRadius = hypot(
                displayX(applicationContext).toDouble(), displayY(
                    applicationContext
                ).toDouble()
            )

            val circularReveal: Animator = ViewAnimationUtils.createCircularReveal(
                takeNoteLayoutBinding.colorPaletteInclude.root,
                (takeNoteLayoutBinding.paintingToolbarInclude.allColorsPicker.x.toInt()),
                (takeNoteLayoutBinding.paintingToolbarInclude.allColorsPicker.y.toInt() - (takeNoteLayoutBinding.paintingToolbarInclude.allColorsPicker.height)),
                finalRadius.toFloat(),
                (takeNoteLayoutBinding.paintingToolbarInclude.allColorsPicker.height.toFloat() / 2)
            )

            circularReveal.duration = 555
            circularReveal.interpolator = AccelerateInterpolator()

            circularReveal.start()
            circularReveal.addListener(object : Animator.AnimatorListener {

                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {

                    takeNoteLayoutBinding.colorPaletteInclude.root.visibility = View.INVISIBLE

                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationStart(animation: Animator?) {

                }

            })

        } else {

            if (!takeNoteLayoutBinding.editTextTitleView.text.isNullOrBlank()
                || !takeNoteLayoutBinding.editTextContentView.text.isNullOrBlank()
                || paintingCanvasView.overallRedrawPaintingData.isNotEmpty()) {

                takeNoteLayoutBinding.waitingViewUpload.visibility = View.VISIBLE

                CoroutineScope(Dispatchers.IO).launch {

                    notesIO.saveNotesAndPaintingOnline(
                        context = this@TakeNote,
                        firebaseUser = firebaseUser,
                        takeNoteLayoutBinding = takeNoteLayoutBinding,
                        databaseEndpoints = databaseEndpoints,
                        paintingIO = paintingIO,
                        paintingCanvasView = paintingCanvasView,
                        contentEncryption = contentEncryption,
                        documentId = documentId
                    )

                    notesIO.saveNotesAndPaintingOffline(
                        context = this@TakeNote,
                        firebaseUser = firebaseUser,
                        takeNoteLayoutBinding = takeNoteLayoutBinding,
                        paintingIO = paintingIO,
                        paintingCanvasView = paintingCanvasView,
                        contentEncryption = contentEncryption,
                        documentId = documentId
                    ).await()

                    withContext(Dispatchers.Main) {

                        takeNoteLayoutBinding.waitingViewUpload.visibility = View.INVISIBLE

                        if (incomingActivity == EntryConfigurations::class.java.simpleName) {

                            startActivity(Intent(applicationContext, KeepNoteOverview::class.java),
                                ActivityOptions.makeCustomAnimation(applicationContext, 0, R.anim.fade_out).toBundle())

                            this@TakeNote.finish()

                        } else {

                            this@TakeNote.finish()
                            overridePendingTransition(0, R.anim.fade_out)

                        }

                    }

                }

            } else {

                if (incomingActivity == EntryConfigurations::class.java.simpleName) {

                    startActivity(Intent(applicationContext, KeepNoteOverview::class.java),
                        ActivityOptions.makeCustomAnimation(applicationContext, 0, R.anim.fade_out).toBundle())

                    this@TakeNote.finish()

                } else {

                    this@TakeNote.finish()
                    overridePendingTransition(0, R.anim.fade_out)

                }

            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val firebaseUser = Firebase.auth.currentUser

        if (firebaseUser != null
            && !firebaseUser.isAnonymous) {

            when (requestCode) {
                NoteConfigurations.AudioRecordRequestCode -> {

                    when (resultCode) {
                        Activity.RESULT_OK -> {
                            Log.d(this@TakeNote.javaClass.simpleName, "Voice Recorded Successfully")

                            Firebase.auth.currentUser?.let { firebaseUser ->

                                audioFilePath?.let { audioFilePath ->

                                    val audioFile = File(audioFilePath)

                                    (application as KeepNoteApplication).firebaseStorage
                                        .getReference(databaseEndpoints.voiceRecordingEndpoint(firebaseUser.uid, documentId.toString()).plus("/${audioFileId}" + ".MP3"))
                                        .putBytes(audioFile.readBytes())
                                        .addOnSuccessListener {
                                            Log.d(this@TakeNote.javaClass.simpleName, "Audio Recorded File Uploaded Successfully")

                                        }

                                }

                            }

                        }
                        else -> {
                            Log.d(this@TakeNote.javaClass.simpleName, "Voice Recording Process Issue")



                        }
                    }

                }
            }

        }

    }

    override fun networkAvailable() {



    }

    override fun networkLost() {



    }

}