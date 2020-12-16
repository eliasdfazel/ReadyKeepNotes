package net.geeksempire.keepnotes.Database.IO

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import net.geeksempire.keepnotes.Database.DataStructure.NotesDataStructure
import net.geeksempire.keepnotes.Database.GeneralEndpoints.DatabaseEndpoints
import net.geeksempire.keepnotes.KeepNoteApplication
import net.geeksempire.keepnotes.Notes.Painting.PaintingCanvasView
import net.geeksempire.keepnotes.databinding.TakeNoteLayoutBinding

class NotesIO (private val keepNoteApplication: KeepNoteApplication) {

    fun saveNotesAndPainting(firebaseUser: FirebaseUser?, takeNoteLayoutBinding: TakeNoteLayoutBinding,
                             databaseEndpoints: DatabaseEndpoints,
                             paintingIO: PaintingIO,
                             paintingCanvasView: PaintingCanvasView,
                             documentId: Long) {

        firebaseUser?.let {

            val notesDataStructure = NotesDataStructure(
                noteTile = takeNoteLayoutBinding.editTextTitleView.text.toString(),
                noteTextContent = takeNoteLayoutBinding.editTextContentView.text.toString(),
                noteHandwritingSnapshotLink = null
            )

            (keepNoteApplication as KeepNoteApplication).firestoreDatabase
                .document(databaseEndpoints.GeneralEndpoints(firebaseUser.uid) + "/" + "${documentId}")
                .set(notesDataStructure)
                .addOnSuccessListener {
                    Log.d(this@NotesIO.javaClass.simpleName, "Note Saved Successfully")

                    (keepNoteApplication as KeepNoteApplication).firebaseStorage
                        .getReference(databaseEndpoints.GeneralEndpoints(firebaseUser.uid) + "/${documentId}.PNG")
                        .putBytes(paintingIO.takeScreenshot(paintingCanvasView))
                        .addOnSuccessListener { uploadTaskSnapshot ->
                            Log.d(this@NotesIO.javaClass.simpleName, "Paint Saved Successfully")

                            (keepNoteApplication as KeepNoteApplication).firebaseStorage
                                .getReference(databaseEndpoints.GeneralEndpoints(firebaseUser.uid) + "/${documentId}.PNG")
                                .downloadUrl
                                .addOnSuccessListener { downloadUrl ->

                                    (keepNoteApplication as KeepNoteApplication).firestoreDatabase
                                        .document(databaseEndpoints.GeneralEndpoints(firebaseUser.uid) + "/" + documentId)
                                        .update(
                                            "noteHandwritingSnapshotLink", downloadUrl.toString(),
                                        ).addOnSuccessListener {
                                            Log.d(this@NotesIO.javaClass.simpleName, "Paint Link Saved Successfully")


                                        }.addOnFailureListener {
                                            Log.d(this@NotesIO.javaClass.simpleName, "Paint Link Did Not Saved")


                                        }

                                }.addOnFailureListener {



                                }

                        }.addOnFailureListener {
                            Log.d(this@NotesIO.javaClass.simpleName, "Paint Did Note Saved")


                        }

                }.addOnFailureListener {
                    Log.d(this@NotesIO.javaClass.simpleName, "Note Did Note Saved")


                }

        }

    }

    fun saveQuickNotes(firebaseUser: FirebaseUser?, takeNoteLayoutBinding: TakeNoteLayoutBinding,
                       databaseEndpoints: DatabaseEndpoints,
                       documentId: Int) {

        firebaseUser?.let {

            val notesDataStructure = NotesDataStructure(
                noteTile = takeNoteLayoutBinding.editTextTitleView.text.toString(),
                noteTextContent = takeNoteLayoutBinding.editTextContentView.text.toString(),
                noteHandwritingSnapshotLink = null
            )

            (keepNoteApplication as KeepNoteApplication).firestoreDatabase
                .document(databaseEndpoints.GeneralEndpoints(firebaseUser.uid) + "/" + "${documentId}")
                .set(notesDataStructure)

        }

    }

}