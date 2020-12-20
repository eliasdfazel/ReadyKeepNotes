package net.geeksempire.keepnotes.Database.IO

import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import net.geeksempire.keepnotes.Database.DataStructure.NotesDataStructure
import net.geeksempire.keepnotes.Database.GeneralEndpoints.DatabaseEndpoints
import net.geeksempire.keepnotes.KeepNoteApplication
import net.geeksempire.keepnotes.Notes.Tools.Painting.PaintingCanvasView
import net.geeksempire.keepnotes.R
import net.geeksempire.keepnotes.Utils.Security.Encryption.ContentEncryption
import net.geeksempire.keepnotes.Utils.UI.NotifyUser.SnackbarActionHandlerInterface
import net.geeksempire.keepnotes.Utils.UI.NotifyUser.SnackbarBuilder
import net.geeksempire.keepnotes.databinding.OverviewLayoutBinding
import net.geeksempire.keepnotes.databinding.TakeNoteLayoutBinding

class NotesIO (private val keepNoteApplication: KeepNoteApplication) {

    fun saveNotesAndPainting(context: Context,
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

            takeNoteLayoutBinding.waitingViewUpload.visibility = View.VISIBLE

            takeNoteLayoutBinding.toggleKeyboardHandwriting.isEnabled = false
            takeNoteLayoutBinding.savingView.isEnabled = false

            val notesDataStructure = NotesDataStructure(
                noteTile = contentEncryption.encryptEncodedData(noteTitle.toString(), firebaseUser.uid).asList().toString(),
                noteTextContent = contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid).asList().toString(),
                noteHandwritingSnapshotLink = null
            )

            (keepNoteApplication).firestoreDatabase
                .document(databaseEndpoints.GeneralEndpoints(firebaseUser.uid) + "/" + "${documentId}")
                .set(notesDataStructure)
                .addOnSuccessListener {
                    Log.d(this@NotesIO.javaClass.simpleName, "Note Saved Successfully")

                    (keepNoteApplication).firebaseStorage
                        .getReference(databaseEndpoints.GeneralEndpoints(firebaseUser.uid) + "/${documentId}.PNG")
                        .putBytes(paintingIO.takeScreenshot(paintingCanvasView))
                        .addOnSuccessListener { uploadTaskSnapshot ->
                            Log.d(this@NotesIO.javaClass.simpleName, "Paint Saved Successfully")

                            (keepNoteApplication).firebaseStorage
                                .getReference(databaseEndpoints.GeneralEndpoints(firebaseUser.uid) + "/${documentId}.PNG")
                                .downloadUrl
                                .addOnSuccessListener { downloadUrl ->

                                    (keepNoteApplication).firestoreDatabase
                                        .document(databaseEndpoints.GeneralEndpoints(firebaseUser.uid) + "/" + documentId)
                                        .update(
                                            "noteHandwritingSnapshotLink", downloadUrl.toString(),
                                        ).addOnSuccessListener {
                                            Log.d(this@NotesIO.javaClass.simpleName, "Paint Link Saved Successfully")

                                            takeNoteLayoutBinding.waitingViewUpload.visibility = View.INVISIBLE

                                            takeNoteLayoutBinding.toggleKeyboardHandwriting.isEnabled = true
                                            takeNoteLayoutBinding.savingView.isEnabled = true

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

                    SnackbarBuilder(context).show (
                        rootView = takeNoteLayoutBinding.rootView,
                        messageText= context.getString(R.string.emptyNotesCollection),
                        messageDuration = Snackbar.LENGTH_INDEFINITE,
                        actionButtonText = R.string.retryText,
                        snackbarActionHandlerInterface = object : SnackbarActionHandlerInterface {

                            override fun onActionButtonClicked(snackbar: Snackbar) {
                                super.onActionButtonClicked(snackbar)

                                saveNotesAndPainting(
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

        }

    }

    fun saveQuickNotes(firebaseUser: FirebaseUser?,
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

                overviewLayoutBinding.waitingViewUpload.visibility = View.VISIBLE

                val inputMethodManager: InputMethodManager by lazy {
                    keepNoteApplication.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                }

                overviewLayoutBinding.quickTakeNote.clearFocus()

                inputMethodManager.hideSoftInputFromWindow(
                    overviewLayoutBinding.quickTakeNote.windowToken,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )

                overviewLayoutBinding.savingView.isEnabled = false

                val documentId: Long = System.currentTimeMillis()

                val notesDataStructure = NotesDataStructure(
                    noteTile = contentEncryption.encryptEncodedData("Untitled Note", firebaseUser.uid).asList().toString(),
                    noteTextContent = contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid).asList().toString()
                )

                (keepNoteApplication).firestoreDatabase
                    .document(databaseEndpoints.GeneralEndpoints(firebaseUser.uid) + "/" + "${documentId}")
                    .set(notesDataStructure)
                    .addOnSuccessListener {

                        overviewLayoutBinding.savingView.isEnabled = true

                        overviewLayoutBinding.quickTakeNote.text = null

                        overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = false
                        overviewLayoutBinding.textInputQuickTakeNote.error = null

                        inputMethodManager.showSoftInput(
                            overviewLayoutBinding.quickTakeNote,
                            InputMethodManager.SHOW_IMPLICIT
                        )

                        overviewLayoutBinding.quickTakeNote.requestFocus()

                        overviewLayoutBinding.waitingViewUpload.visibility = View.INVISIBLE

                    }.addOnFailureListener {

                        overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = true
                        overviewLayoutBinding.textInputQuickTakeNote.error = keepNoteApplication.getString(R.string.errorOccurred)

                    }

            } else {

                overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = true
                overviewLayoutBinding.textInputQuickTakeNote.error = keepNoteApplication.getString(R.string.noNotesTyped)

            }

        }

    }

}