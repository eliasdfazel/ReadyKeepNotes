package net.geeksempire.keepnote.Notes.Taking.Extensions

import net.geeksempire.keepnote.Notes.Taking.TakeNote
import net.geeksempire.keepnote.Notes.UI.PaintingCanvasView
import net.geeksempire.keepnote.R

fun TakeNote.setupTakeNoteTheme() {

    takeNoteLayoutBinding.paintingCanvasContainer.addView(
        PaintingCanvasView(applicationContext).also { paintingCanvasView ->
            paintingCanvasView.setupPaintingPanel(
                getColor(R.color.default_color_game_light),
                10.0f
            )
        }
    )

    if (themePreferences.checkLightDark()) {

        takeNoteLayoutBinding.rootView.setBackgroundColor(getColor(R.color.light))

        window.statusBarColor = getColor(R.color.light)
        window.navigationBarColor = getColor(R.color.light)

    } else {

        takeNoteLayoutBinding.rootView.setBackgroundColor(getColor(R.color.dark))

        window.statusBarColor = getColor(R.color.dark)
        window.navigationBarColor = getColor(R.color.dark)

    }


}