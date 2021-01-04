package net.geeksempire.ready.keep.notes.Overview.UserInterface.Extensions

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.R

fun KeepNoteOverview.startDatabaseOperation() = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

    val allNotesData = (application as KeepNoteApplication)
        .notesRoomDatabaseConfiguration
        .getAllNotesData()

    notesOverviewViewModel.notesDatabaseQuerySnapshots.postValue(allNotesData)

}

/**
 * - False to Load Evey Time Database Changed
 * - True Just Add Database Change Listener
 **/
fun KeepNoteOverview.startNetworkOperation() {

    firebaseUser?.let {

        //

    }

}

fun KeepNoteOverview.loadUserAccountInformation() {

    firebaseUser?.let {

        Glide.with(applicationContext)
            .load(firebaseUser.photoUrl)
            .error(getDrawable(R.drawable.not_login_icon))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transform(CircleCrop())
            .into(overviewLayoutBinding.profileImageView)

    }

}