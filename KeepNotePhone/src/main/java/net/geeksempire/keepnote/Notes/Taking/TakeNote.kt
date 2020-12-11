package net.geeksempire.keepnote.Notes.Taking

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import net.geeksempire.keepnote.Notes.Taking.Extensions.setupTakeNoteTheme
import net.geeksempire.keepnote.Notes.Taking.Extensions.setupToggleKeyboardHandwriting
import net.geeksempire.keepnote.Notes.UI.PaintingCanvasView
import net.geeksempire.keepnote.Preferences.Theme.ThemePreferences
import net.geeksempire.keepnote.R
import net.geeksempire.keepnote.databinding.TakeNoteLayoutBinding

class TakeNote : AppCompatActivity() {

    val paintingCanvasView: PaintingCanvasView by lazy {
        PaintingCanvasView(applicationContext).also {
            it.setupPaintingPanel(
                getColor(R.color.default_color_light),
                7.0f
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

    lateinit var takeNoteLayoutBinding: TakeNoteLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        takeNoteLayoutBinding = TakeNoteLayoutBinding.inflate(layoutInflater)
        setContentView(takeNoteLayoutBinding.root)

        setupTakeNoteTheme()

        setupToggleKeyboardHandwriting()

        takeNoteLayoutBinding.savingView.setOnClickListener {

            paintingCanvasView.changePaintingColor()


        }

        takeNoteLayoutBinding.savingView.setOnLongClickListener {

            paintingCanvasView.removeAllPaints()

            true
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}