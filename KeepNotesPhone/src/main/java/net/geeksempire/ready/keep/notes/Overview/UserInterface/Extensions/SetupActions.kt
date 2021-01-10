package net.geeksempire.ready.keep.notes.Overview.UserInterface.Extensions

import android.app.ActivityOptions
import android.content.Intent
import androidx.core.app.ActivityOptionsCompat
import net.geeksempire.ready.keep.notes.AccountManager.UserInterface.AccountInformation
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.Preferences.UserInterface.PreferencesControl
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface.SearchProcess

fun KeepNoteOverview.setupActions() {

    overviewLayoutBinding.fullNoteTaking.setOnClickListener {

        startActivity(Intent(applicationContext, TakeNote::class.java).apply {
            putExtra(TakeNote.NoteConfigurations.ExtraConfigurations, TakeNote.NoteConfigurations.Handwriting)
            putExtra(TakeNote.NoteExtraData.ContentText, overviewLayoutBinding.quickTakeNote.text.toString())
        }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.fade_in, 0).toBundle())

    }

    overviewLayoutBinding.savingView.setOnClickListener {

        notesIO.saveQuickNotesOnline(
            context = this@setupActions,
            firebaseUser = firebaseUser,
            overviewLayoutBinding = overviewLayoutBinding,
            contentEncryption = contentEncryption,
            databaseEndpoints = databaseEndpoints
        )

        notesIO.saveQuickNotesOffline(
            context = this@setupActions,
            firebaseUser = firebaseUser,
            contentEncryption = contentEncryption
        )

    }

    overviewLayoutBinding.startNewNoteView.setOnClickListener {

        startActivity(Intent(applicationContext, TakeNote::class.java).apply {
            putExtra(TakeNote.NoteConfigurations.ExtraConfigurations, TakeNote.NoteConfigurations.KeyboardTyping)
            putExtra(TakeNote.NoteExtraData.ContentText, overviewLayoutBinding.quickTakeNote.text.toString())
        }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.fade_in, 0).toBundle())

    }

    overviewLayoutBinding.goToSearch.setOnClickListener {

        val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this@setupActions, overviewLayoutBinding.goToSearch, "transition")

        startActivity(Intent(applicationContext, SearchProcess::class.java).apply {
            putExtra("xPosition", (overviewLayoutBinding.goToSearch.x + (overviewLayoutBinding.goToSearch.width / 2)).toInt())
            putExtra("yPosition", (overviewLayoutBinding.goToSearch.y + (overviewLayoutBinding.goToSearch.height / 2)).toInt())
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }, activityOptionsCompat.toBundle())

    }

    overviewLayoutBinding.profileImageView.setOnClickListener {

        val accountInformation = Intent(applicationContext, AccountInformation::class.java)
        startActivity(
            accountInformation,
            ActivityOptions.makeSceneTransitionAnimation(
                this@setupActions, overviewLayoutBinding.profileImageView, getString(
                    R.string.profileImageTransitionName
                )
            ).toBundle()
        )

    }

    overviewLayoutBinding.preferencesView.setOnClickListener {

        val accountInformation = Intent(applicationContext, PreferencesControl::class.java)
        startActivity(
            accountInformation,
            ActivityOptions.makeSceneTransitionAnimation(
                this@setupActions, overviewLayoutBinding.preferencesView, getString(
                    R.string.preferenceImageTransitionName
                )
            ).toBundle()
        )

    }
}