package net.geeksempire.ready.keep.notes.Overview.UserInterface.Extensions

import android.app.ActivityOptions
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityOptionsCompat
import net.geeksempire.ready.keep.notes.AccountManager.UserInterface.AccountInformation
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.Preferences.UserInterface.PreferencesControl
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface.SearchProcess

fun KeepNoteOverview.setupActions() {

    overviewLayoutBinding.fullNoteTaking.setOnClickListener {

        Handler(Looper.getMainLooper()).postDelayed({

            TakeNote.open(context = applicationContext,
                incomingActivityName = this@setupActions.javaClass.simpleName,
                extraConfigurations = TakeNote.NoteConfigurations.Handwriting,
                uniqueNoteId = documentId,
                contentText = overviewLayoutBinding.quickTakeNote.text.toString(),
                encryptedTextContent = false
            )

        }, 333)

    }

    overviewLayoutBinding.savingView.setOnClickListener {

        documentId = System.currentTimeMillis()

        notesIO.saveQuickNotesOnline(
            documentId = documentId,
            context = this@setupActions,
            firebaseUser = firebaseUser,
            overviewLayoutBinding = overviewLayoutBinding,
            contentEncryption = contentEncryption,
            databaseEndpoints = databaseEndpoints
        )

        notesIO.saveQuickNotesOffline(
            documentId = documentId,
            context = this@setupActions,
            firebaseUser = firebaseUser,
            contentEncryption = contentEncryption
        )

    }

    overviewLayoutBinding.startNewNoteView.setOnClickListener {

        Handler(Looper.getMainLooper()).postDelayed({

            TakeNote.open(context = applicationContext,
                incomingActivityName = this@setupActions.javaClass.simpleName,
                extraConfigurations = TakeNote.NoteConfigurations.KeyboardTyping,
                uniqueNoteId = documentId,
                contentText = overviewLayoutBinding.quickTakeNote.text.toString(),
                encryptedTextContent = false
            )

        }, 333)

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