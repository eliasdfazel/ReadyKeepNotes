package net.geeksempire.keepnote.Notes.Taking

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import net.geeksempire.keepnote.KeepNoteApplication
import net.geeksempire.keepnote.Notes.Painting.PaintingCanvasView
import net.geeksempire.keepnote.Notes.Taking.Extensions.setupPaintingActions
import net.geeksempire.keepnote.Notes.Taking.Extensions.setupTakeNoteTheme
import net.geeksempire.keepnote.Notes.Taking.Extensions.setupToggleKeyboardHandwriting
import net.geeksempire.keepnote.Preferences.Theme.ThemePreferences
import net.geeksempire.keepnote.R
import net.geeksempire.keepnote.Utils.Network.NetworkConnectionListener
import net.geeksempire.keepnote.Utils.Network.NetworkConnectionListenerInterface
import net.geeksempire.keepnote.databinding.TakeNoteLayoutBinding
import javax.inject.Inject

class TakeNote : AppCompatActivity(), NetworkConnectionListenerInterface {

    val paintingCanvasView: PaintingCanvasView by lazy {
        PaintingCanvasView(applicationContext).also {
            it.setupPaintingPanel(
                getColor(R.color.default_color_light),
                7.777f
            )
        }
    }

    val inputMethodManager: InputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    val themePreferences: ThemePreferences by lazy {
        ThemePreferences(applicationContext)
    }

    /**
     * True: Handwriting - False: Keyboard
     **/
    var toggleKeyboardHandwriting: Boolean = false

    @Inject
    lateinit var networkConnectionListener: NetworkConnectionListener

    lateinit var takeNoteLayoutBinding: TakeNoteLayoutBinding

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

        takeNoteLayoutBinding.savingView.setOnClickListener {



        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {

        startActivity(Intent(Intent.ACTION_MAIN).apply {
            this.addCategory(Intent.CATEGORY_HOME)
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }, ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())

    }

    override fun networkAvailable() {



    }

    override fun networkLost() {



    }

}