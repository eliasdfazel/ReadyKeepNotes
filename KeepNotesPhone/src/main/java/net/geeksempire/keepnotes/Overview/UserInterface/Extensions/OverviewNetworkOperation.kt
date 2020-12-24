package net.geeksempire.keepnotes.Overview.UserInterface.Extensions

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.firestore.Query
import net.geeksempire.keepnotes.Database.DataStructure.Notes
import net.geeksempire.keepnotes.KeepNoteApplication
import net.geeksempire.keepnotes.Overview.UserInterface.KeepNoteOverview

fun KeepNoteOverview.startNetworkOperation() {

    firebaseUser?.let {

        (application as KeepNoteApplication)
            .firestoreDatabase.collection(databaseEndpoints.generalEndpoints(firebaseUser.uid))

        databaseListener = (application as KeepNoteApplication)
            .firestoreDatabase.collection(databaseEndpoints.generalEndpoints(firebaseUser.uid))
            .orderBy(Notes.NoteIndex, Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, firestoreException ->

                querySnapshot?.let {

                    notesOverviewViewModel.processDocumentSnapshots(querySnapshot.documents)

                }

                firestoreException?.printStackTrace()
            }

        Glide.with(applicationContext)
            .load(firebaseUser.photoUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transform(CircleCrop())
            .into(overviewLayoutBinding.profileImageView)

    }

}