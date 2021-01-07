package net.geeksempire.ready.keep.notes.Notes.Taking.Extensions

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.Display.DpToInteger

@SuppressLint("ClickableViewAccessibility")
fun TakeNote.setupTakeNoteTheme() {

    takeNoteLayoutBinding.paintingCanvasContainer.addView(paintingCanvasView)

    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthSample.addView(strokePaintingCanvasView)

    when (themePreferences.checkThemeLightDark()) {
        ThemeType.ThemeLight -> {

            window.statusBarColor = getColor(R.color.light)
            window.navigationBarColor = getColor(R.color.light)

            window.decorView.systemUiVisibility = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }

            takeNoteLayoutBinding.rootView.setBackgroundColor(getColor(R.color.light))
            takeNoteLayoutBinding.editTextTitleView.setBackgroundColor(getColor(R.color.lighter))

            takeNoteLayoutBinding.editTextContentView.setTextColor(getColor(R.color.dark))

            takeNoteLayoutBinding.toggleKeyboardHandwriting.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_light))
            takeNoteLayoutBinding.savingView.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_light))

        }
        ThemeType.ThemeDark -> {

            window.statusBarColor = getColor(R.color.dark)
            window.navigationBarColor = getColor(R.color.dark)

            takeNoteLayoutBinding.rootView.setBackgroundColor(getColor(R.color.dark))
            takeNoteLayoutBinding.editTextTitleView.setBackgroundColor(getColor(R.color.darker))

            takeNoteLayoutBinding.editTextContentView.setTextColor(getColor(R.color.light))

            takeNoteLayoutBinding.toggleKeyboardHandwriting.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_dark))
            takeNoteLayoutBinding.savingView.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_dark))

        }
    }

    val toggleClickHandler = Handler(Looper.getMainLooper())
    var toggleClickRunnable: Runnable? = null
    takeNoteLayoutBinding.toggleKeyboardHandwriting.setOnTouchListener { view, motionEvent ->

        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {

                toggleClickRunnable = Runnable {

                    contentDescriptionShowing = true

                    val toggleLayoutParams = takeNoteLayoutBinding.toggleKeyboardHandwriting.layoutParams as ConstraintLayout.LayoutParams
                    toggleLayoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    takeNoteLayoutBinding.toggleKeyboardHandwriting.layoutParams = toggleLayoutParams

                    takeNoteLayoutBinding.toggleKeyboardHandwriting.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_END
                    takeNoteLayoutBinding.toggleKeyboardHandwriting.text = takeNoteLayoutBinding.toggleKeyboardHandwriting.contentDescription

                }

                toggleClickRunnable?.let { toggleClickHandler.postDelayed(it, 531) }

            }
            MotionEvent.ACTION_UP -> {

                Handler(Looper.getMainLooper()).postDelayed({

                    contentDescriptionShowing = false

                }, 333)

                val toggleLayoutParams = takeNoteLayoutBinding.toggleKeyboardHandwriting.layoutParams as ConstraintLayout.LayoutParams
                toggleLayoutParams.width = DpToInteger(applicationContext, 53)
                takeNoteLayoutBinding.toggleKeyboardHandwriting.layoutParams = toggleLayoutParams

                takeNoteLayoutBinding.toggleKeyboardHandwriting.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
                takeNoteLayoutBinding.toggleKeyboardHandwriting.text = ""

                toggleClickRunnable?.let { toggleClickHandler.removeCallbacks(it) }

            }
        }

        false
    }

    val savingClickHandler = Handler(Looper.getMainLooper())
    var savingClickRunnable: Runnable? = null
    takeNoteLayoutBinding.savingView.setOnTouchListener { view, motionEvent ->

        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {

                savingClickRunnable = Runnable {

                    contentDescriptionShowing = true

                    val savingLayoutParams = takeNoteLayoutBinding.savingView.layoutParams as ConstraintLayout.LayoutParams
                    savingLayoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    takeNoteLayoutBinding.savingView.layoutParams = savingLayoutParams

                    takeNoteLayoutBinding.toggleKeyboardHandwriting.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
                    takeNoteLayoutBinding.savingView.text = takeNoteLayoutBinding.savingView.contentDescription

                }

                savingClickRunnable?.let { savingClickHandler.postDelayed(it, 531) }

            }
            MotionEvent.ACTION_UP -> {

                Handler(Looper.getMainLooper()).postDelayed({

                    contentDescriptionShowing = false

                }, 333)

                val savingLayoutParams = takeNoteLayoutBinding.savingView.layoutParams as ConstraintLayout.LayoutParams
                savingLayoutParams.width = DpToInteger(applicationContext, 53)
                takeNoteLayoutBinding.savingView.layoutParams = savingLayoutParams

                takeNoteLayoutBinding.toggleKeyboardHandwriting.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
                takeNoteLayoutBinding.savingView.text = ""

                savingClickRunnable?.let { savingClickHandler.removeCallbacks(it) }

            }
        }

        false
    }

}