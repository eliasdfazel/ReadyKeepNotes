package net.geeksempire.ready.keep.notes.Overview.UserInterface.Extensions

import android.app.ActivityOptions
import android.content.Intent
import net.geeksempire.ready.keep.notes.AccountManager.UserInterface.AccountInformation
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.InApplicationReview.InApplicationReviewProcess

fun KeepNoteOverview.setupActions() {

    overviewLayoutBinding.fullNoteTaking.setOnClickListener {

        startActivity(Intent(applicationContext, TakeNote::class.java).apply {
            putExtra(TakeNote.NoteTakingWritingType.ExtraConfigurations, TakeNote.NoteTakingWritingType.Handwriting)
            putExtra(TakeNote.NoteTakingWritingType.ContentText, overviewLayoutBinding.quickTakeNote.text.toString())
        }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.fade_in, 0).toBundle())

    }

    overviewLayoutBinding.savingView.setOnClickListener {

        notesIO.saveQuickNotes(firebaseUser = firebaseUser,
            overviewLayoutBinding = overviewLayoutBinding,
            contentEncryption = contentEncryption,
            databaseEndpoints = databaseEndpoints)

    }

    overviewLayoutBinding.applicationLogoView.setOnClickListener {

        InApplicationReviewProcess(this@setupActions)
            .start(true)

    }

    overviewLayoutBinding.profileImageView.setOnClickListener {

        val accountInformation = Intent(applicationContext, AccountInformation::class.java)
        startActivity(accountInformation,
            ActivityOptions.makeSceneTransitionAnimation(this@setupActions, overviewLayoutBinding.profileImageView, getString(R.string.profileImageTransitionName)).toBundle())

    }

}