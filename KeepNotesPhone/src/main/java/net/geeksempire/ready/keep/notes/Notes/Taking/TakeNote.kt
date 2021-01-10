package net.geeksempire.ready.keep.notes.Notes.Taking

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase
import net.geeksempire.ready.keep.notes.Database.IO.NotesIO
import net.geeksempire.ready.keep.notes.Database.IO.PaintingIO
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.EntryConfigurations
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Notes.Taking.Extensions.setupPaintingActions
import net.geeksempire.ready.keep.notes.Notes.Taking.Extensions.setupTakeNoteTheme
import net.geeksempire.ready.keep.notes.Notes.Taking.Extensions.setupToggleKeyboardHandwriting
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Adapter.RecentColorsAdapter
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
import javax.inject.Inject
import kotlin.math.hypot

class TakeNote : AppCompatActivity(), NetworkConnectionListenerInterface {

    val paintingCanvasView: PaintingCanvasView by lazy {
        PaintingCanvasView(applicationContext).also {
            it.setupPaintingPanel(
                getColor(R.color.default_color),
                3.0f
            )
        }
    }

    val strokePaintingCanvasView: StrokePaintingCanvasView by lazy {
        StrokePaintingCanvasView(applicationContext).also {
            it.setupPaintingPanel(
                getColor(R.color.default_color),
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

    val contentEncryption: ContentEncryption  = ContentEncryption()

    /**
     * True: Handwriting - False: Keyboard
     **/
    var toggleKeyboardHandwriting: Boolean = false

    val firebaseUser = Firebase.auth.currentUser

    val databaseEndpoints: DatabaseEndpoints = DatabaseEndpoints()

    val recentColorsAdapter: RecentColorsAdapter by lazy {
        RecentColorsAdapter(this@TakeNote, paintingCanvasView)
    }

    var autoEnterPlaced = false

    var contentDescriptionShowing = false

    var incomingActivity = EntryConfigurations::class.java.simpleName

    @Inject lateinit var networkCheckpoint: NetworkCheckpoint

    @Inject lateinit var networkConnectionListener: NetworkConnectionListener

    lateinit var takeNoteLayoutBinding: TakeNoteLayoutBinding

    object NoteConfigurations {
        const val ExtraConfigurations = "NoteTakingWritingType"
        const val KeyboardTyping = "KeyboardTyping"
        const val Handwriting = "Handwriting"
        const val VoiceRecording = "VoiceRecording"

        const val EncryptedTextContent = "EncryptedTextContent"
    }

    object NoteExtraData {
        const val DocumentId = "DocumentId"
        const val TitleText = "TitleText"
        const val ContentText = "ContentText"
        const val PaintingPath = "PaintingPath"
    }

    companion object {
        val paintingPathsJsonArray: ArrayList<DocumentSnapshot> = ArrayList<DocumentSnapshot>()
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

        if (intent.hasExtra("IncomingActivityName")) {

            incomingActivity = intent.getStringExtra("IncomingActivityName")

        }

        firebaseUser?.let { firebaseUser ->

            val documentId = if (intent.hasExtra(NoteExtraData.DocumentId)) {

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

            if (intent.hasExtra(NoteExtraData.ContentText)) {

                println(">>>>>>>>>>>>>>>>>>>> " + intent.getStringExtra(NoteExtraData.ContentText))

                takeNoteLayoutBinding.editTextContentView.setText(intent.getStringExtra(NoteExtraData.ContentText)?.let { contentEncryption.decryptEncodedData(it, firebaseUser.uid) })

            }

            if (intent.hasExtra(NoteExtraData.PaintingPath)) {

                val paintingPathsData = intent.getStringExtra(NoteExtraData.PaintingPath)!!

                paintingIO.preparePaintingPathsOffline(paintingCanvasView, paintingPathsData).invokeOnCompletion {

                    paintingCanvasView.restorePaints()

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

                    notesIO.saveNotesAndPaintingOffline(
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

            if (incomingActivity == EntryConfigurations::class.java.simpleName) {

                startActivity(Intent(applicationContext, KeepNoteOverview::class.java))

            } else {



            }

            this@TakeNote.finish()
            overridePendingTransition(0, R.anim.fade_out)

        }

    }

    override fun networkAvailable() {



    }

    override fun networkLost() {



    }

}