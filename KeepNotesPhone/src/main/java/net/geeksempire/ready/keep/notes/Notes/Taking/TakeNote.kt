package net.geeksempire.ready.keep.notes.Notes.Taking

import android.animation.Animator
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
import net.geeksempire.ready.keep.notes.Database.GeneralEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.Database.IO.NotesIO
import net.geeksempire.ready.keep.notes.Database.IO.PaintingIO
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Notes.Taking.Extensions.setupPaintingActions
import net.geeksempire.ready.keep.notes.Notes.Taking.Extensions.setupTakeNoteTheme
import net.geeksempire.ready.keep.notes.Notes.Taking.Extensions.setupToggleKeyboardHandwriting
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Adapter.RecentColorsAdapter
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.PaintingCanvasView
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Utils.StrokePaintingCanvasView
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemePreferences
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.Extensions.checkSpecialCharacters
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

    @Inject
    lateinit var networkConnectionListener: NetworkConnectionListener

    lateinit var takeNoteLayoutBinding: TakeNoteLayoutBinding

    object NoteTakingWritingType {
        const val ExtraConfigurations = "NoteTakingWritingType"
        const val Keyboard = "Keyboard"
        const val Handwriting = "Handwriting"

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

        firebaseUser?.let { firebaseUser ->

            val documentId = if (intent.hasExtra(NoteTakingWritingType.DocumentId)) {

                intent.getLongExtra(NoteTakingWritingType.DocumentId, System.currentTimeMillis())

            } else {

                System.currentTimeMillis()

            }

            if (intent.hasExtra(NoteTakingWritingType.TitleText)) {

                takeNoteLayoutBinding.editTextTitleView.setText(intent.getStringExtra(
                    NoteTakingWritingType.TitleText
                )?.let {
                    contentEncryption.decryptEncodedData(it, firebaseUser.uid)
                })

            }

            if (intent.hasExtra(NoteTakingWritingType.ContentText)) {

                takeNoteLayoutBinding.editTextContentView.setText(intent.getStringExtra(
                    NoteTakingWritingType.ContentText
                )?.let {
                    contentEncryption.decryptEncodedData(it, firebaseUser.uid)
                })

            }

            if (intent.hasExtra(NoteTakingWritingType.PaintingPath)) {

                paintingIO.preparePaintingPaths(paintingCanvasView, paintingPathsJsonArray).invokeOnCompletion {

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

                    }

                }

            })

            takeNoteLayoutBinding.savingView.setOnClickListener {

                notesIO.saveNotesAndPainting(
                    context = this@TakeNote,
                    firebaseUser = firebaseUser,
                    takeNoteLayoutBinding = takeNoteLayoutBinding,
                    databaseEndpoints = databaseEndpoints,
                    paintingIO = paintingIO,
                    paintingCanvasView = paintingCanvasView,
                    contentEncryption = contentEncryption,
                    documentId = documentId
                )

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

            this@TakeNote.finish()
            overridePendingTransition(0, R.anim.fade_out)

        }

    }

    override fun networkAvailable() {



    }

    override fun networkLost() {



    }

}