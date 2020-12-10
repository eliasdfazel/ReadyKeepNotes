package net.geeksempire.keepnote.Notes.Taking.Extensions

import net.geeksempire.keepnote.Notes.Taking.TakeNote
import net.geeksempire.keepnote.R

fun TakeNote.setupTakeNoteTheme() {

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