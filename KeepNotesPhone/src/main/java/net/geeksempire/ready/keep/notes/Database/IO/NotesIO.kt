package net.geeksempire.ready.keep.notes.Database.IO

import android.content.Context
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.abanabsalan.aban.magazine.Utils.System.hideKeyboard
import com.abanabsalan.aban.magazine.Utils.System.showKeyboard
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import net.geeksempire.ready.keep.notes.ContentContexts.NetworkOperations.NaturalLanguageProcessNetworkOperation
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDataStructure
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.Database.GeneralEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.Database.Json.JsonIO
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.PaintingCanvasView
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.Security.Encryption.ContentEncryption
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarActionHandlerInterface
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarBuilder
import net.geeksempire.ready.keep.notes.databinding.OverviewLayoutBinding
import net.geeksempire.ready.keep.notes.databinding.TakeNoteLayoutBinding

class NotesIO (private val keepNoteApplication: KeepNoteApplication) {

    private val jsonIO = JsonIO()

    fun saveNotesAndPaintingOffline(context: TakeNote,
                                    firebaseUser: FirebaseUser?,
                                    takeNoteLayoutBinding: TakeNoteLayoutBinding,
                                    paintingIO: PaintingIO,
                                    paintingCanvasView: PaintingCanvasView,
                                    contentEncryption: ContentEncryption,
                                    documentId: Long) = CoroutineScope(Dispatchers.IO).async {

        firebaseUser?.let {

            try {

                withContext(SupervisorJob() + Dispatchers.Main) {

                    takeNoteLayoutBinding.waitingViewUpload.visibility = View.VISIBLE

                    takeNoteLayoutBinding.toggleKeyboardHandwriting.isEnabled = false
                    takeNoteLayoutBinding.savingView.isEnabled = false

                }

                val noteTitle = takeNoteLayoutBinding.editTextTitleView.text?:"Untitled Note"
                val contentText = takeNoteLayoutBinding.editTextContentView.text?:"No Content"

                val noteHandwritingSnapshot = context.openFileOutput("${documentId}.PNG", Context.MODE_PRIVATE)

                noteHandwritingSnapshot.write(paintingIO.takeScreenshot(paintingCanvasView))

                val notesDatabaseModel = NotesDatabaseModel(uniqueNoteId = documentId,
                    noteTile = contentEncryption.encryptEncodedData(noteTitle.toString(), firebaseUser.uid).asList().toString(),
                    noteTextContent = contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid).asList().toString(),
                    noteHandwritingPaintingPaths = jsonIO.writeAllPaintingPathData(paintingCanvasView.overallRedrawPaintingData),
                    noteHandwritingSnapshotLink = context.getFileStreamPath("${documentId}.PNG").absolutePath,
                    noteTakenTime = documentId,
                    noteEditTime = null,
                    noteIndex = documentId
                )

                (keepNoteApplication).notesRoomDatabaseConfiguration
                    .insertNewNoteData(notesDatabaseModel)

                withContext(SupervisorJob() + Dispatchers.Main) {

                    takeNoteLayoutBinding.waitingViewUpload.visibility = View.INVISIBLE

                    takeNoteLayoutBinding.toggleKeyboardHandwriting.isEnabled = true
                    takeNoteLayoutBinding.savingView.isEnabled = true

                }

            } catch (e: Exception) {
                e.printStackTrace()

                withContext(SupervisorJob() + Dispatchers.Main) {

                    Log.d(this@NotesIO.javaClass.simpleName, "Note Did Note Saved")

                    SnackbarBuilder(context).show (
                        rootView = takeNoteLayoutBinding.rootView,
                        messageText= context.getString(R.string.emptyNotesCollection),
                        messageDuration = Snackbar.LENGTH_INDEFINITE,
                        actionButtonText = R.string.retryText,
                        snackbarActionHandlerInterface = object : SnackbarActionHandlerInterface {

                            override fun onActionButtonClicked(snackbar: Snackbar) {
                                super.onActionButtonClicked(snackbar)



                            }

                        }
                    )

                }

            }

        }

    }

    fun saveNotesAndPaintingOnline(context: TakeNote,
                                   firebaseUser: FirebaseUser?,
                                   takeNoteLayoutBinding: TakeNoteLayoutBinding,
                                   databaseEndpoints: DatabaseEndpoints,
                                   paintingIO: PaintingIO,
                                   paintingCanvasView: PaintingCanvasView,
                                   contentEncryption: ContentEncryption,
                                   documentId: Long) {

        firebaseUser?.let {

            val noteTitle = takeNoteLayoutBinding.editTextTitleView.text?:"Untitled Note"
            val contentText = takeNoteLayoutBinding.editTextContentView.text?:"No Content"

            val notesDataStructure = NotesDataStructure(
                noteTile = contentEncryption.encryptEncodedData(noteTitle.toString(), firebaseUser.uid).asList().toString(),
                noteTextContent = contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid).asList().toString(),
                noteHandwritingSnapshotLink = null,
                noteIndex = documentId
            )

            val databasePath = databaseEndpoints.generalEndpoints(firebaseUser.uid) + "/" + "${documentId}"

            /* Save Notes & Snapshot Of Handwriting */
            (keepNoteApplication).firestoreDatabase
                .document(databasePath)
                .set(notesDataStructure)
                .addOnSuccessListener {
                    Log.d(this@NotesIO.javaClass.simpleName, "Note Saved Successfully")

                    (keepNoteApplication).firebaseStorage
                        .getReference(databaseEndpoints.generalEndpoints(firebaseUser.uid) + "/${documentId}.PNG")
                        .putBytes(paintingIO.takeScreenshot(paintingCanvasView))
                        .addOnSuccessListener { uploadTaskSnapshot ->
                            Log.d(this@NotesIO.javaClass.simpleName, "Paint Saved Successfully")

                            (keepNoteApplication).firebaseStorage
                                .getReference(databaseEndpoints.generalEndpoints(firebaseUser.uid) + "/${documentId}.PNG")
                                .downloadUrl
                                .addOnSuccessListener { downloadUrl ->

                                    (keepNoteApplication).firestoreDatabase
                                        .document(databaseEndpoints.generalEndpoints(firebaseUser.uid) + "/" + documentId)
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

                    if (contentText.isNotBlank()
                        || contentText != "No Content") {

                        NaturalLanguageProcessNetworkOperation(context)
                            .start(
                                firebaseUserId = firebaseUser.uid,
                                documentId = documentId.toString(),
                                textContent = contentText.toString()
                            )

                    }

                }.addOnFailureListener {
                    Log.d(this@NotesIO.javaClass.simpleName, "Note Did Note Saved")

                    SnackbarBuilder(context).show (
                        rootView = takeNoteLayoutBinding.rootView,
                        messageText= context.getString(R.string.emptyNotesCollection),
                        messageDuration = Snackbar.LENGTH_INDEFINITE,
                        actionButtonText = R.string.retryText,
                        snackbarActionHandlerInterface = object : SnackbarActionHandlerInterface {

                            override fun onActionButtonClicked(snackbar: Snackbar) {
                                super.onActionButtonClicked(snackbar)

                                saveNotesAndPaintingOnline(
                                    context,
                                    firebaseUser,
                                    takeNoteLayoutBinding,
                                    databaseEndpoints,
                                    paintingIO,
                                    paintingCanvasView,
                                    contentEncryption,
                                    documentId
                                )

                            }

                        }
                    )

                }

            /* Save Text Content Archive */
            (keepNoteApplication).firestoreDatabase
                .collection(databaseEndpoints.noteTextsEndpoints(databasePath))
                .add(hashMapOf(
                    "noteTile" to notesDataStructure.noteTile,
                    "noteTextContent" to notesDataStructure.noteTextContent
                ))
                .addOnSuccessListener {
                    Log.d(this@NotesIO.javaClass.simpleName, "Note Archive Saved Successfully")


                }.addOnFailureListener {



                }

            /* Save Paths Of Handwriting Notes */
            (keepNoteApplication).firestoreDatabase
                .collection(databaseEndpoints.paintPathsCollectionEndpoints(databasePath))
                .get().addOnSuccessListener {

                    it.documents.forEach { documentSnapshot ->

                        documentSnapshot.reference.delete()

                    }

                    paintingCanvasView.overallRedrawPaintingData.forEach { aPathXY ->

                        (keepNoteApplication).firestoreDatabase
                            .collection(databaseEndpoints.paintPathsCollectionEndpoints(databasePath))
                            .add(
                                hashMapOf(
                                    "paintPath" to jsonIO.writePaintingPathData(aPathXY)
                                )
                            )
                            .addOnSuccessListener {
                                Log.d(this@NotesIO.javaClass.simpleName, "Handwriting Paths Saved Successfully")



                            }.addOnFailureListener {


                            }

                    }

                }

        }

    }


    fun saveQuickNotesOffline(context: AppCompatActivity,
                              firebaseUser: FirebaseUser?,
                              overviewLayoutBinding: OverviewLayoutBinding,
                              contentEncryption: ContentEncryption) = CoroutineScope(Dispatchers.IO).launch {

        firebaseUser?.let {

            if (overviewLayoutBinding.quickTakeNote.text?.isNotBlank() == true) {

                try {

                    withContext(SupervisorJob() + Dispatchers.Main) {

                        if (overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled) {

                            overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = false
                            overviewLayoutBinding.textInputQuickTakeNote.error = null

                        }

                        overviewLayoutBinding.waitingViewUpload.visibility = View.VISIBLE

                        overviewLayoutBinding.quickTakeNote.clearFocus()

                        hideKeyboard(context, overviewLayoutBinding.quickTakeNote)

                        overviewLayoutBinding.savingView.isEnabled = false

                    }

                    val contentText = overviewLayoutBinding.quickTakeNote.text?:"No Content"

                    val documentId: Long = System.currentTimeMillis()

                    val notesDatabaseModel = NotesDatabaseModel(uniqueNoteId = documentId,
                        noteTile = null,
                        noteTextContent = contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid).asList().toString(),
                        noteHandwritingPaintingPaths = null,
                        noteHandwritingSnapshotLink = null,
                        noteTakenTime = documentId,
                        noteEditTime = null,
                        noteIndex = documentId
                    )

                    (keepNoteApplication).notesRoomDatabaseConfiguration
                        .insertNewNoteData(notesDatabaseModel)

                    withContext(SupervisorJob() + Dispatchers.Main) {

                        overviewLayoutBinding.waitingViewUpload.visibility = View.INVISIBLE

                        overviewLayoutBinding.savingView.isEnabled = true

                        overviewLayoutBinding.quickTakeNote.text = null

                        overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = false
                        overviewLayoutBinding.textInputQuickTakeNote.error = null

                        showKeyboard(context, overviewLayoutBinding.quickTakeNote)

                        overviewLayoutBinding.quickTakeNote.requestFocus()

                    }

                } catch (e: Exception) {
                    e.printStackTrace()

                    withContext(SupervisorJob() + Dispatchers.Main) {

                        overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = true
                        overviewLayoutBinding.textInputQuickTakeNote.error = keepNoteApplication.getString(R.string.offlineSavingError)

                    }

                }

            } else {

                withContext(SupervisorJob() + Dispatchers.Main) {

                    overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = true
                    overviewLayoutBinding.textInputQuickTakeNote.error = keepNoteApplication.getString(R.string.noNotesTyped)

                }

            }

        }

    }

    fun saveQuickNotesOnline(context: AppCompatActivity,
                             firebaseUser: FirebaseUser?,
                             overviewLayoutBinding: OverviewLayoutBinding,
                             contentEncryption: ContentEncryption,
                             databaseEndpoints: DatabaseEndpoints) {

        firebaseUser?.let {

            if (overviewLayoutBinding.quickTakeNote.text?.isNotBlank() == true) {

                if (overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled) {

                    overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = false
                    overviewLayoutBinding.textInputQuickTakeNote.error = null

                }

                val contentText = overviewLayoutBinding.quickTakeNote.text?:"No Content"

                val documentId: Long = System.currentTimeMillis()

                val notesDataStructure = NotesDataStructure(
                    noteTile = contentEncryption.encryptEncodedData("Untitled Note", firebaseUser.uid).asList().toString(),
                    noteTextContent = contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid).asList().toString(),
                    noteIndex = documentId
                )

                (keepNoteApplication).firestoreDatabase
                    .document(databaseEndpoints.generalEndpoints(firebaseUser.uid) + "/" + "${documentId}")
                    .set(notesDataStructure)
                    .addOnSuccessListener {

                        if (contentText.isNotBlank()
                            || contentText != "No Content") {

                            NaturalLanguageProcessNetworkOperation(context)
                                .start(
                                    firebaseUserId = firebaseUser.uid,
                                    documentId = documentId.toString(),
                                    textContent =  contentText.toString()
                                )

                        }

                    }.addOnFailureListener {

                        overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = true
                        overviewLayoutBinding.textInputQuickTakeNote.error = keepNoteApplication.getString(R.string.onlineSavingError)

                    }

            } else {

                overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = true
                overviewLayoutBinding.textInputQuickTakeNote.error = keepNoteApplication.getString(R.string.noNotesTyped)

            }

        }

    }

}