package net.geeksempire.keepnotes.Notes.Taking

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase
import net.geeksempire.keepnotes.Database.GeneralEndpoints.DatabaseEndpoints
import net.geeksempire.keepnotes.Database.IO.NotesIO
import net.geeksempire.keepnotes.Database.IO.PaintingIO
import net.geeksempire.keepnotes.KeepNoteApplication
import net.geeksempire.keepnotes.Notes.Taking.Extensions.setupPaintingActions
import net.geeksempire.keepnotes.Notes.Taking.Extensions.setupTakeNoteTheme
import net.geeksempire.keepnotes.Notes.Taking.Extensions.setupToggleKeyboardHandwriting
import net.geeksempire.keepnotes.Notes.Tools.Painting.Adapter.RecentColorsAdapter
import net.geeksempire.keepnotes.Notes.Tools.Painting.PaintingCanvasView
import net.geeksempire.keepnotes.Preferences.Theme.ThemePreferences
import net.geeksempire.keepnotes.R
import net.geeksempire.keepnotes.Utils.Network.NetworkConnectionListener
import net.geeksempire.keepnotes.Utils.Network.NetworkConnectionListenerInterface
import net.geeksempire.keepnotes.Utils.Security.Encryption.ContentEncryption
import net.geeksempire.keepnotes.databinding.TakeNoteLayoutBinding
import javax.inject.Inject

class TakeNote : AppCompatActivity(), NetworkConnectionListenerInterface {

    val paintingCanvasView: PaintingCanvasView by lazy {
        PaintingCanvasView(applicationContext).also {
            it.setupPaintingPanel(
                getColor(R.color.default_color_light),
                3.7531f
            )
        }
    }

    val inputMethodManager: InputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

            val documentId = if (intent.hasExtra(TakeNote.NoteTakingWritingType.DocumentId)) {

                intent.getLongExtra(TakeNote.NoteTakingWritingType.DocumentId, System.currentTimeMillis())

            } else {

                System.currentTimeMillis()

            }



            if (intent.hasExtra(TakeNote.NoteTakingWritingType.TitleText)) {

                takeNoteLayoutBinding.editTextTitleView.setText(intent.getStringExtra(TakeNote.NoteTakingWritingType.TitleText)?.let {
                    contentEncryption.decryptEncodedData(it, firebaseUser.uid)
                })

            }

            if (intent.hasExtra(TakeNote.NoteTakingWritingType.ContentText)) {

                takeNoteLayoutBinding.editTextContentView.setText(intent.getStringExtra(TakeNote.NoteTakingWritingType.ContentText)?.let {
                    contentEncryption.decryptEncodedData(it, firebaseUser.uid)
                })

            }

            if (intent.hasExtra(TakeNote.NoteTakingWritingType.PaintingPath)) {

                paintingIO.preparePaintingPaths(paintingCanvasView, TakeNote.paintingPathsJsonArray).invokeOnCompletion {

                    paintingCanvasView.restorePaints()

                }

            }

            takeNoteLayoutBinding.savingView.setOnClickListener {

                notesIO.saveNotesAndPainting(
                    context = applicationContext,
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

        this@TakeNote.finish()
        overridePendingTransition(0, R.anim.fade_out)

    }

    override fun networkAvailable() {



    }

    override fun networkLost() {



    }

}